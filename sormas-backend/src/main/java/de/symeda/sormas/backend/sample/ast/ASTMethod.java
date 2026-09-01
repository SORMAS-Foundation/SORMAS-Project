package de.symeda.sormas.backend.sample.ast;

/**
 * Measure method for: Antibiotic susceptibility testing.
 */
public enum ASTMethod {
	/** Tests susceptibility at a defined antimicrobial breakpoint. */
	BREAKPOINT,
	/** Determines susceptibility using an antimicrobial gradient strip. */
	E_TEST,
	/** Determines the minimum inhibitory concentration. */
	MIC
}
