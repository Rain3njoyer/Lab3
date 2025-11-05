# Use Case 08 — Delete Employee Record

**Goal in Context:** As an HR advisor I want to delete an employee's details so that the company is compliant with data retention legislation.

**Primary Actor:** HR Advisor

**Preconditions:** HR authorization to delete (policy), DB backup available.

**Trigger:** HR selects "Delete employee" and confirms.

**Main Success Scenario:**
1. HR chooses employee to remove.
2. System prompts confirmation and warns about legal compliance.
3. HR confirms.
4. System deletes or anonymizes record and writes audit log.
5. System confirms deletion.

**Extensions:** insufficient permissions (deny), DB error.

**Success Condition:** Employee data removed or anonymized per policy.

**Failed Condition:** Employee not removed.
