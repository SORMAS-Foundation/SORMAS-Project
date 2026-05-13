package de.symeda.sormas.backend;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Can be used to write UNIT tests to test a class in an isolated manner.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractUnitTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
}
