# Use Case 07 — Update Employee Record

**Goal in Context:** As an HR advisor I want to update an employee's details so that employee's details are kept up-to-date.

**Primary Actor:** HR Advisor

**Preconditions:** DB accessible, employee exists.

**Trigger:** HR opens employee record and clicks "Edit".

**Main Success Scenario:**
1. HR edits fields (contact, title change, salary change).
2. System validates, persists changes (possibly add new `title` or `salary` records).
3. System confirms update.

**Extensions:** validation error, concurrency conflict.

**Success Condition:** Updated details persisted.

**Failed Condition:** Changes not saved.
