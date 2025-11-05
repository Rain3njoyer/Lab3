# Use Case 05 — Add a New Employee

**Goal in Context:** As an HR advisor I want to add a new employee's details so that I can ensure the new employee is paid.

**Primary Actor:** HR Advisor

**Preconditions:**
- HR logged in (if auth required).
- DB accessible.

**Trigger:** HR selects "Add new employee".

**Main Success Scenario:**
1. HR opens Add Employee form.
2. HR fills mandatory fields (name, DOB, department, job title, salary, start date).
3. HR submits form.
4. System validates inputs and writes to `employees`, `titles`, `salaries`, `dept_emp`.
5. System confirms creation and shows new employee record.

**Extensions:** validation errors (show messages), DB error -> show message.

**Success Condition:** Employee record exists in DB.

**Failed Condition:** Employee not created.
