package de.symeda.sormas.backend.sormastosormas.validation;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

public class InfraValidationSoundnessTest extends AbstractBeanTest {

	private static final TypeResolver typeResolver = new TypeResolver();
	private static final MemberResolver memberResolver = new MemberResolver(typeResolver);

	/**
	 * We treat these classes as leave nodes, we do not descend further.
	 */
	private final Set<Class<?>> ignoreLeaves =
		new HashSet<>(Arrays.asList(Date.class, String.class, Double.class, Float.class, Integer.class, Boolean.class));

	/**
	 * Represents a graph or tree node. Carries data and a human readable label.
	 * 
	 * @param <N>
	 *            The type of the contained data field
	 */
	private static class Node<N> {

		N data;
		String label;

		public Node(N data, String label) {
			this.data = data;
			this.label = label;
		}
	}

	/**
	 * Represents a tree data structure.
	 * 
	 * @param <N>
	 *            The type of the contained data field nodes.
	 */
	private static class Tree<N> {

		Node<N> node;
		List<Tree<N>> children;

		public Tree(N data, String label) {
			this.node = new Node<>(data, label);
		}

		@Override
		public String toString() {
			return treeToString(0);
		}

		private String treeToString(int indent) {
			String prefix = StringUtils.repeat("", indent);
			StringBuilder s = new StringBuilder();
			s.append(prefix).append(node.data).append(" : ").append(node.label);
			for (Tree<N> child : children) {
				s.append("\n").append(child.treeToString(indent + 2));
			}
			return s.toString();
		}
	}

	/**
	 * Represents a Dto as a tree structure.
	 */
	private static class DtoTree extends Tree<ResolvedField> {

		public DtoTree(ResolvedField rootField) {
			super(rootField, rootField.getType().toString());
		}
	}

	/**
	 * This is the root node we need to make the recursion work.
	 */
	private static class DtoRootNode<T> {

		T dtoUnderTest;

		public DtoRootNode(T dtoUnderTest) {
			this.dtoUnderTest = dtoUnderTest;
		}

	}

	/**
	 * Resolve all fields of the given type
	 * 
	 * @param type
	 *            the resolved type
	 * @return all resolved fields contained in type
	 */
	private List<ResolvedField> getAllFieldsForType(ResolvedType type) {
		ResolvedTypeWithMembers resolvedTypeWithMembers = memberResolver.resolve(type, null, null);
		ResolvedField[] memberFields = resolvedTypeWithMembers.getMemberFields();
		ResolvedField[] staticFields = resolvedTypeWithMembers.getStaticFields();
		return Stream.concat(Arrays.stream(memberFields), Arrays.stream(staticFields)).filter(this::isNotLeaf).collect(Collectors.toList());
	}

	/**
	 * Start from the root node and resolve all child fields
	 * 
	 * @param rootNode
	 *            The root node through which subfields we want to walk through
	 * @return A complete @{@link DtoTree}
	 */
	private DtoTree buildDtoTree(DtoTree rootNode) {
		final ResolvedField rootField = rootNode.node.data;
		final ResolvedType curType = rootField.getType();

		if (curType.isInstanceOf(Map.class)) {
			if (!curType.getErasedType().equals(Map.class)) {
				throw new NotImplementedException("Ping @JonasCir");
			}
			// if the current dto field is a map, analyze both the key and the value class
			List<ResolvedType> mapParams = curType.getTypeParameters();
			if (isNotLeaf(mapParams.get(0)) && isNotLeaf(mapParams.get(1))) {
				return buildMap(rootNode, mapParams);
			}
		}

		if (curType.isInstanceOf(Collection.class)) {
			if (!curType.isInstanceOf(List.class)) {
				throw new NotImplementedException("Ping @JonasCir");
			}
			// if the current dto field is a list, analyze the list type
			ResolvedType listParam = curType.getTypeParameters().get(0);
			if (isNotLeaf(listParam)) {
				return buildList(rootNode, listParam);
			}
		}

		// We have a normal type: Find all subfields and build a tree
		rootNode.children = getAllFieldsForType(rootField.getType()).stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());
		return rootNode;
	}

	/**
	 * @param dtoTree
	 *            the current tree root
	 * @param listParam
	 *            the list parameters we
	 * @return the complete tree
	 */
	private DtoTree buildList(DtoTree dtoTree, ResolvedType listParam) {
		final ResolvedType resolvedElem = typeResolver.resolve(listParam.getErasedType());
		List<ResolvedField> resolvedElemField = getAllFieldsForType(resolvedElem);
		dtoTree.children = resolvedElemField.stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());
		return dtoTree;
	}

	/**
	 * @param dtoTree
	 *            the current tree root
	 * @param mapParams
	 *            the key and value types of the map field
	 * @return the complete tree
	 */
	private DtoTree buildMap(DtoTree dtoTree, List<ResolvedType> mapParams) {
		// walk the key type
		final ResolvedType resolvedKey = typeResolver.resolve(mapParams.get(0).getErasedType());
		List<ResolvedField> resolvedKeyFields = getAllFieldsForType(resolvedKey);
		List<Tree<ResolvedField>> keyChildren = resolvedKeyFields.stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());

		// walk the value type
		final ResolvedType resolvedValue = typeResolver.resolve(mapParams.get(1).getErasedType());
		List<ResolvedField> resolvedValueFields = getAllFieldsForType(resolvedValue);
		List<Tree<ResolvedField>> valueChildren = resolvedValueFields.stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());

		dtoTree.children = Stream.concat(keyChildren.stream(), valueChildren.stream()).collect(Collectors.toList());
		return dtoTree;
	}

	/**
	 * @param type
	 *            the type to check
	 * @return true if the current type is not considered a leaf and should be walked
	 */
	private boolean isNotLeaf(ResolvedType type) {
		// todo fix isEnum und typ.isPrimitive
		final boolean ignored = ignoreLeaves.contains(type.getErasedType()) || type.getErasedType().isEnum() || type instanceof ResolvedPrimitiveType;
		return !ignored;
	}

	/**
	 * @param field
	 *            the field to check
	 * @return true if the current field type is not considered a leaf and should be walked
	 */
	private boolean isNotLeaf(ResolvedField field) {
		return isNotLeaf(field.getType());
	}

	/**
	 * Given a complete tree, create paths to each field which contains infrastructure data
	 * 
	 * @param dtoTree
	 *            the @{@link DtoTree} from which we extract all field paths
	 * @return A list of paths to infra fields. Each list element described how to reach the infra field from the root.
	 */
	private List<ResolvedField[]> extractInfraFieldPaths(Tree<ResolvedField> dtoTree) {
		List<ResolvedField[]> ret = new ArrayList<>();
		ResolvedField currentNode = dtoTree.node.data;
		List<Tree<ResolvedField>> currentChildren = dtoTree.children;

		for (Tree<ResolvedField> subtree : currentChildren) {
			if (subtree.children.isEmpty()) {
				// we reached a leaf node
				final String className = subtree.node.data.getType().getErasedType().getName();
				final boolean isInfrastructureDto = isInfrastructureDto(className);
				if (isInfrastructureDto) {
					ret.add(
						new ResolvedField[] {
							currentNode,
							subtree.node.data });
				}
			} else {
				// not a leaf, continue walk
				List<ResolvedField[]> childPaths = extractInfraFieldPaths(subtree);
				// add current prefix in front of every child prefix
				ret.addAll(
					childPaths.stream()
						.map(
							// concatenate currentPath prefix with the child path
							p -> ArrayUtils.insert(0, p, currentNode))
						.collect(Collectors.toList()));
			}
		}
		return ret;
	}

	private boolean isInfrastructureDto(String className) {
		return className.startsWith("de.symeda.sormas.api.infrastructure.")
			&& !className.startsWith("de.symeda.sormas.api.infrastructure.pointofentry.")
			&& !className.startsWith("de.symeda.sormas.api.infrastructure.facility.");
	}

	/**
	 * Follow the path and inject objects along it. Once we reach the leaf (i.e.,
	 * a @{@link de.symeda.sormas.api.InfrastructureDataReferenceDto},
	 * we tag it with a label.
	 * 
	 * @param path
	 *            the path we need to walk from dto root to infra field
	 * @param currentObject
	 *            the current object we are modifying fields on
	 * @param caption
	 *            the caption we put on the leaf infra ref dto
	 */
	private void injectWrongInfra(Queue<ResolvedField> path, Object currentObject, String caption)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		ResolvedField current = path.poll();

		if (current == null) {
			return;
		}

		// first elem is always the dto itself, skip it
		if (current.getName().equals("dtoUnderTest")) {
			current = path.poll();
			if (current == null) {
				return;
			}
		}

		final ResolvedField currentField = current;

		Field injectionPoint = getField(currentField.getName(), currentObject);
		injectionPoint.setAccessible(true);

		// find out if the field is already initialized
		Object childObject = injectionPoint.get(currentObject);
		if (childObject == null) {
			// populate the field

			if (path.peek() == null) {
				// we reached a leaf, get the InfrastructureDataReferenceDto(String uuid, String caption, String externalId) constructor
				// in case this call errors, make sure that a constructor (String uuid, String caption, String externalId) is available in
				// the reference DTO.
				Constructor<?> constructor = currentField.getType().getErasedType().getConstructor(String.class, String.class, String.class);
				childObject = constructor.newInstance(DataHelper.createConstantUuid(0), caption, "");
			} else {
				// we still descend through the object tree
				Constructor<?> constructor = currentField.getType().getErasedType().getConstructor();
				childObject = constructor.newInstance();
			}
			// assign the created object to the current field
			injectionPoint.set(currentObject, childObject);
		}

		// if the current field is a list we need to descend through the elements of the list, not the list object itself
		if (currentField.getType().isInstanceOf(Collection.class)) {
			if (!currentField.getType().isInstanceOf(List.class)) {
				throw new NotImplementedException("Ping @JonasCir");
			}

			List list = (List) childObject;
			if (list.isEmpty()) {
				// we just created the list, create a new element for the list
				ResolvedType elementType = currentField.getType().getTypeParameters().get(0);
				Constructor<?> constructor = elementType.getErasedType().getConstructor();
				childObject = constructor.newInstance();
				list.add(childObject);
			} else {
				childObject = list.get(0);
			}
		}

		if (currentField.getType().isInstanceOf(Map.class)) {
			throw new NotImplementedException("Ping @JonasCir");
		}

		currentObject = childObject;
		injectWrongInfra(path, currentObject, caption);

	}

	/**
	 * Get all field of t from its class and all superclasses
	 * 
	 * @param fieldName
	 * @param t
	 *            The object we want the fields of
	 * @param <T>
	 *            The type of the object
	 * @return All fields contained in object t fof type T
	 */
	private <T> Field getField(String fieldName, T t) throws NoSuchFieldException {
		Class<?> clazz = t.getClass();
		while (clazz != Object.class) {

			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}

		}
		throw new NoSuchFieldException();
	}

	/**
	 * @param rootNode
	 *            the root node containing a @{@link SormasToSormasEntityDto}
	 * @param <T>
	 *            the type of the @{@link SormasToSormasEntityDto} tested
	 * @return all paths to all @{@link de.symeda.sormas.api.InfrastructureDataReferenceDto} fields contained in type T
	 */
	private <T> List<ResolvedField[]> getInfraPaths(DtoRootNode<T> rootNode) {
		// make the recursion work, we need a class which wraps the S2S Dto we want to test. We resolve, the dtoUnderTest
		// field and go from there.
		ResolvedType resolve = typeResolver.resolve(rootNode.getClass());
		ResolvedTypeWithMembers resolvedTypeWithMembers = memberResolver.resolve(resolve, null, null);
		ResolvedField dtoUnderTestField = resolvedTypeWithMembers.getMemberFields()[0];
		DtoTree dtoTree = new DtoTree(dtoUnderTestField);
		DtoTree walkedTree = buildDtoTree(dtoTree);
		return extractInfraFieldPaths(walkedTree);
	}

	/**
	 * Find all field paths we expect to be rejected by validation logic.
	 * 
	 * @param entityDto
	 *            the entity object we inject the wrong infrastructure into
	 * @param paths
	 *            all paths leading to infrastructure fields
	 * @return the set of all property paths we expect to turn up in validation
	 * 
	 */
	private <T extends SormasToSormasShareableDto> Set<String> getExpectedPaths(SormasToSormasEntityDto<T> entityDto, List<ResolvedField[]> paths)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		Set<String> expected = new HashSet<>();

		for (ResolvedField[] path : paths) {
			ArrayDeque<ResolvedField> queue = new ArrayDeque<>(Arrays.asList(path));
			String caption = queue.stream().map(ResolvedMember::getName).collect(Collectors.joining("."));
			expected.add(caption);
			injectWrongInfra(queue, entityDto, caption);
		}
		return expected;
	}

	/**
	 * Get all field paths which were rejected by validation logic.
	 * 
	 * @param entityDto
	 *            the entity object we injected the wrong infrastructure into
	 * @param validator
	 *            the validator to be used
	 * 
	 * @return a list of rejected fields
	 */
	private <DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto> Set<String> getRejectedFields(
		SHARED entityDto,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator) {
		ValidationErrors errors = validator.validateIncoming(entityDto);
		return errors.getSubGroups()
			.stream()
			.map(ValidationErrorGroup::getMessages)
			.flatMap(Collection::stream)
			.map(ValidationErrorMessage::getArgs)
			.flatMap(Arrays::stream)
			.map(Object::toString)
			.collect(Collectors.toSet());
	}

	private <T, DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto> void assertValidation(
		SHARED entity,
		DtoRootNode<T> rootNode,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		List<ResolvedField[]> paths = getInfraPaths(rootNode);
		Set<String> expected = getExpectedPaths(entity, paths);
		Set<String> foundFields = getRejectedFields(entity, validator);
		// smoke test, in case both are empty for some reason this will blow up
		assertFalse(foundFields.isEmpty());
		assertFalse(expected.isEmpty());
		final Collection disjunction = CollectionUtils.disjunction(foundFields, expected);
		assertTrue(disjunction.isEmpty(), "The following fields are not validated in the DTO: " + disjunction);
	}

	@Test
	public void testShareCaseValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class CaseDtoRootNode extends DtoRootNode<SormasToSormasCaseDto> {

			public CaseDtoRootNode(SormasToSormasCaseDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasCaseDto caseDto = new SormasToSormasCaseDto();
		CaseDtoRootNode rootNode = new CaseDtoRootNode(caseDto);
		assertValidation(caseDto, rootNode, getSormasToSormasCaseDtoValidator());
	}

	@Test
	public void testShareContactValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class ContactDtoRootNode extends DtoRootNode<SormasToSormasContactDto> {

			public ContactDtoRootNode(SormasToSormasContactDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasContactDto contactDto = new SormasToSormasContactDto();
		ContactDtoRootNode rootNode = new ContactDtoRootNode(contactDto);
		assertValidation(contactDto, rootNode, getSormasToSormasContactDtoValidator());
	}

	@Test
	public void testShareEventValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class EventDtoRootNode extends DtoRootNode<SormasToSormasEventDto> {

			public EventDtoRootNode(SormasToSormasEventDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasEventDto eventDto = new SormasToSormasEventDto();
		EventDtoRootNode rootNode = new EventDtoRootNode(eventDto);
		assertValidation(eventDto, rootNode, getSormasToSormasEventDtoValidator());
	}

	@Test
	public void testShareEventParticipantValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class EventParticipantDtoRootNode extends DtoRootNode<SormasToSormasEventParticipantDto> {

			public EventParticipantDtoRootNode(SormasToSormasEventParticipantDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasEventParticipantDto eventParticipantDto = new SormasToSormasEventParticipantDto();
		EventParticipantDtoRootNode rootNode = new EventParticipantDtoRootNode(eventParticipantDto);
		assertValidation(eventParticipantDto, rootNode, getSormasToSormasEventParticipantDtoValidator());
	}

	@Test
	public void testShareImmunizationValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class ImmunizationDtoRootNode extends DtoRootNode<SormasToSormasImmunizationDto> {

			public ImmunizationDtoRootNode(SormasToSormasImmunizationDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasImmunizationDto immunizationDto = new SormasToSormasImmunizationDto();
		ImmunizationDtoRootNode rootNode = new ImmunizationDtoRootNode(immunizationDto);
		assertValidation(immunizationDto, rootNode, getSormasToSormasImmunizationDtoValidator());
	}

	@Test
	@Ignore("lab messages are handled in a nonstandard way")
	public void testShareLabMessageValidationIncoming() {

	}

	@Test
	@Ignore("samples depend on facilities which are not yet supported")
	public void testShareSampleValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		class SampleDtoRootNode extends DtoRootNode<SormasToSormasSampleDto> {

			public SampleDtoRootNode(SormasToSormasSampleDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasSampleDto sampleDto = new SormasToSormasSampleDto();
		SampleDtoRootNode rootNode = new SampleDtoRootNode(sampleDto);
		assertValidation(sampleDto, rootNode, getSormasToSormasSampleDtoValidator());
	}

}
