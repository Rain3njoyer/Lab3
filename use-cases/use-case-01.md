# Use Case 01 — Produce Salary Report (All Employees)

**Goal in Context:** As an HR advisor I want to produce a report on the salary of all employees so that I can support financial reporting of the organisation.

**Scope:** HR System

**Level:** User goal

**Primary Actor:** HR Advisor

**Preconditions:**
- User authenticated (optional for lab).
- Employee database is available and populated.
- System can connect to the database.

**Trigger:** HR Advisor selects "Salary Report — All" from the application menu.

**Main Success Scenario:**
1. HR selects the "Salary Report — All" action.
2. System queries the employees + current salaries.
3. System aggregates/sorts results (by employee number).
4. System displays report screen and provides export/print option.

**Extensions:**
- 2a. Database connection fails -> show error message, allow retry.
- 3a. No employees found -> show "No data" message.

**Postconditions / Success Condition:**
- A list of all employees with current salaries is displayed and available to export/print.

**Failed Condition:**
- Report not produced; clear error shown to user.

**Notes / Schedule:**
- Deliver in Sprint 1.
