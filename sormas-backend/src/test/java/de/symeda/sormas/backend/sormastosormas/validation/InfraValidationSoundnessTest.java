package de.symeda.sormas.backend.sormastosormas.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
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
import org.apache.commons.lang.NotImplementedException;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;

import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;

import static org.junit.Assert.assertEquals;

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

		private static final int indent = 2;

		private String treeToString(int indent) {
			String prefix = String.join("", Collections.nCopies(indent, " "));
			StringBuilder s = new StringBuilder();
			s.append(prefix).append(node.data).append(" : ").append(node.label);
			for (Tree<N> child : children) {
				s.append("\n").append(child.treeToString(indent + Tree.indent));
			}
			return s.toString();
		}
	}

	/**
	 * Represents a Dto as a tree structure.
	 */
	private static class DtoTree extends Tree<DtoData> {

		public DtoTree(ResolvedField rootField) {
			super(new DtoData(rootField), rootField.getType().toString());
		}
	}

	/**
	 * The data type used in @{@link DtoTree} nodes.
	 */
	private static class DtoData {

		ResolvedField field;

		public DtoData(ResolvedField resolvedField) {
			this.field = resolvedField;
		}

		@Override
		public String toString() {
			return field.toString();
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
		final ResolvedField rootField = rootNode.node.data.field;
		final ResolvedType curType = rootField.getType();

		if (curType.getErasedType().equals(Map.class)) {
			// if the current dto field is a map, analyze both the key and the value class
			List<ResolvedType> mapParams = curType.getTypeParameters();
			if (isNotLeaf(mapParams.get(0)) && isNotLeaf(mapParams.get(1))) {
				return buildMap(rootNode, mapParams);
			}
		}

		if (curType.getErasedType().equals(List.class)) {
			// if the current dto field is a list, analyze the list type
			ResolvedType listParam = curType.getTypeParameters().get(0);
			if (isNotLeaf(listParam)) {
				return buildList(rootNode, listParam);
			}
		}

		// We have a normal type: Find all subfields and build a tree 

		List<ResolvedField> childFields = getAllFieldsForType(rootField.getType());
		List<Tree<DtoData>> children = new ArrayList<>();
		for (ResolvedField childField : childFields) {
			final DtoTree newTree = new DtoTree(childField);
			DtoTree childTree = buildDtoTree(newTree);
			children.add(childTree);
		}

		rootNode.children = children;
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
		List<Tree<DtoData>> keyChildren = resolvedKeyFields.stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());

		// walk the value type
		final ResolvedType resolvedValue = typeResolver.resolve(mapParams.get(0).getErasedType());
		List<ResolvedField> resolvedValueFields = getAllFieldsForType(resolvedValue);
		List<Tree<DtoData>> valueChildren = resolvedValueFields.stream().map(DtoTree::new).map(this::buildDtoTree).collect(Collectors.toList());

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
	private List<DtoData[]> extractInfraFieldPaths(Tree<DtoData> dtoTree) {
		List<DtoData[]> ret = new ArrayList<>();
		DtoData currentNode = dtoTree.node.data;
		List<Tree<DtoData>> currentChildren = dtoTree.children;

		for (Tree<DtoData> subtree : currentChildren) {
			if (subtree.children.isEmpty()) {
				// we reached a leaf node
				final String pkgName = subtree.node.data.field.getType().getErasedType().getName();
				final boolean isInfrastructureDto =
					pkgName.startsWith("de.symeda.sormas.api.infrastructure.") && !pkgName.contains("pointofentry") && !pkgName.contains("facility");
				if (isInfrastructureDto) {
					ret.add(
						new DtoData[] {
							currentNode,
							subtree.node.data });
				}
			} else {
				// not a leaf, continue walk
				List<DtoData[]> childPaths = extractInfraFieldPaths(subtree);
				// add current prefix in front of every child prefix
				final List<DtoData[]> completePaths = childPaths.stream()
					.map(
						// concatenate currentPath prefix with the child path
						p -> Stream.concat(
							Arrays.stream(
								new DtoData[] {
									currentNode }),
							Arrays.stream(p)).toArray(DtoData[]::new))
					.collect(Collectors.toList());
				ret.addAll(completePaths);
			}
		}
		return ret;
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
	private void injectWrongInfra(Queue<DtoData> path, Object currentObject, String caption)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		DtoData current = path.poll();

		if (current == null) {
			return;
		}

		// first elem is always the dto itself, skip it
		if (current.field.getName().equals("dtoUnderTest")) {
			current = path.poll();
			if (current == null) {
				return;
			}
		}

		final ResolvedField currentField = current.field;
		Object chillObject;

		// get access to all fields of the current object
		List<Field> availableFields = getFields(currentObject);

		// given the found fields, find our target field which we want to set
		Field injectionPoint = availableFields.stream().filter(f -> f.getName().equals(currentField.getName())).collect(Collectors.toList()).get(0);
		injectionPoint.setAccessible(true);

		// find out if the field is already initialized
		chillObject = injectionPoint.get(currentObject);
		if (chillObject == null) {
			// populate the field

			if (path.peek() == null) {
				// we reached a leaf, get the InfrastructureDataReferenceDto(String uuid, String caption, String externalId) constructor
				Constructor<?> constructor = currentField.getType().getErasedType().getConstructor(String.class, String.class, String.class);
				chillObject = constructor.newInstance(DataHelper.createConstantUuid(0), caption, "");
			} else {
				// we still descend through the object tree
				Constructor<?> constructor = currentField.getType().getErasedType().getConstructor();
				chillObject = constructor.newInstance();
			}
			// assign the created object to the current field
			injectionPoint.set(currentObject, chillObject);
		}

		// if the current field is a list we need to descend through the elements of the list, not the list object itself
		if (currentField.getType().isInstanceOf(List.class)) {
			List list = (List) chillObject;
			if (list.isEmpty()) {
				// we just created the list, create a new element for the list
				ResolvedType elementType = currentField.getType().getTypeParameters().get(0);
				Constructor<?> constructor = elementType.getErasedType().getConstructor();
				chillObject = constructor.newInstance();
				list.add(chillObject);
			} else {
				chillObject = list.get(0);
			}
		}

		if (currentField.getType().isInstanceOf(Map.class)) {
			throw new NotImplementedException();
		}

		currentObject = chillObject;
		injectWrongInfra(path, currentObject, caption);

	}

	/**
	 * Get all field of t from its class and all superclasses
	 * 
	 * @param t
	 *            The object we want the fields of
	 * @param <T>
	 *            The type of the object
	 * @return All fields contained in object t fof type T
	 */
	private <T> List<Field> getFields(T t) {
		List<Field> fields = new ArrayList<>();
		Class<?> clazz = t.getClass();
		while (clazz != Object.class) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	/**
	 * @param rootNode
	 *            the root node containing a @{@link SormasToSormasEntityDto}
	 * @param <T>
	 *            the type of the @{@link SormasToSormasEntityDto} tested
	 * @return all paths to all @{@link de.symeda.sormas.api.InfrastructureDataReferenceDto} fields contained in type T
	 */
	private <T> List<DtoData[]> getInfraPaths(DtoRootNode<T> rootNode) {
		final ResolvedType resolve = typeResolver.resolve(rootNode.getClass());
		final ResolvedTypeWithMembers resolvedTypeWithMembers = memberResolver.resolve(resolve, null, null);
		final ResolvedField resolvedField = resolvedTypeWithMembers.getMemberFields()[0];
		DtoTree caseDtoTree = new DtoTree(resolvedField);
		DtoTree walkedTree = buildDtoTree(caseDtoTree);
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
	private <T extends SormasToSormasShareableDto> Set<String> getExpectedPaths(SormasToSormasEntityDto<T> entityDto, List<DtoData[]> paths)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Set<String> expected = new HashSet<>();
		for (DtoData[] path : paths) {
			ArrayDeque<DtoData> queue = new ArrayDeque<>(Arrays.asList(path));
			String caption = queue.stream().map(d -> d.field.getName()).collect(Collectors.joining("."));
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
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator,
		int expectedSize)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		List<DtoData[]> paths = getInfraPaths(rootNode);
		Set<String> expected = getExpectedPaths(entity, paths);
		Set<String> foundFields = getRejectedFields(entity, validator);
		// smoke test, in case both are empty some reason this will blow up
		assertEquals(expected.size(), expectedSize);
		assertEquals(foundFields, expected);
	}

	@Test
	public void testShareCaseValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class CaseDtoRootNode extends DtoRootNode<SormasToSormasCaseDto> {

			public CaseDtoRootNode(SormasToSormasCaseDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasCaseDto caseDto = new SormasToSormasCaseDto();
		CaseDtoRootNode rootNode = new CaseDtoRootNode(caseDto);
		assertValidation(caseDto, rootNode, getSormasToSormasCaseDtoValidator(), 41);
	}

	@Test
	public void testShareContactValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class ContactDtoRootNode extends DtoRootNode<SormasToSormasContactDto> {

			public ContactDtoRootNode(SormasToSormasContactDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasContactDto ContactDto = new SormasToSormasContactDto();
		ContactDtoRootNode rootNode = new ContactDtoRootNode(ContactDto);
		assertValidation(ContactDto, rootNode, getSormasToSormasContactDtoValidator(), 33);
	}

	@Test
	public void testShareEventValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class EventDtoRootNode extends DtoRootNode<SormasToSormasEventDto> {

			public EventDtoRootNode(SormasToSormasEventDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasEventDto EventDto = new SormasToSormasEventDto();
		EventDtoRootNode rootNode = new EventDtoRootNode(EventDto);
		assertValidation(EventDto, rootNode, getSormasToSormasEventDtoValidator(), 6);
	}

	@Test
	public void testShareEventParticipantValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class EventParticipantDtoRootNode extends DtoRootNode<SormasToSormasEventParticipantDto> {

			public EventParticipantDtoRootNode(SormasToSormasEventParticipantDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasEventParticipantDto EventParticipantDto = new SormasToSormasEventParticipantDto();
		EventParticipantDtoRootNode rootNode = new EventParticipantDtoRootNode(EventParticipantDto);
		assertValidation(EventParticipantDto, rootNode, getSormasToSormasEventParticipantDtoValidator(), 19);
	}

	@Test
	public void testShareImmunizationValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class ImmunizationDtoRootNode extends DtoRootNode<SormasToSormasImmunizationDto> {

			public ImmunizationDtoRootNode(SormasToSormasImmunizationDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasImmunizationDto ImmunizationDto = new SormasToSormasImmunizationDto();
		ImmunizationDtoRootNode rootNode = new ImmunizationDtoRootNode(ImmunizationDto);
		assertValidation(ImmunizationDto, rootNode, getSormasToSormasImmunizationDtoValidator(), 4);
	}

	@Test
	@Ignore("lab messages are handled in a nonstandard way")
	public void testShareLabMessageValidationIncoming() {

	}

	@Test
	@Ignore("samples depend on facilities which are not yet supported")
	public void testShareSampleValidationIncoming()
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		class SampleDtoRootNode extends DtoRootNode<SormasToSormasSampleDto> {

			public SampleDtoRootNode(SormasToSormasSampleDto dtoUnderTest) {
				super(dtoUnderTest);
			}
		}

		SormasToSormasSampleDto SampleDto = new SormasToSormasSampleDto();
		SampleDtoRootNode rootNode = new SampleDtoRootNode(SampleDto);
		assertValidation(SampleDto, rootNode, getSormasToSormasSampleDtoValidator(), 41);
	}

}
