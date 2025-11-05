# Use Case 02 — Produce Salary Report (Department)

**Goal in Context:** As an HR advisor I want to produce a report on the salary of employees in a department so that I can support financial reporting of the organisation.

**Scope:** HR System

**Primary Actor:** HR Advisor

**Preconditions:**
- Department list exists.
- Database accessible.

**Trigger:** HR selects department and "SalaryReportByDepartment".

**Main Success Scenario:**
1. HR chooses department from picklist.
2. System queries DB for employees in the chosen department with current salary.
3. System displays/sorts results.

**Extensions:** invalid department id, DB error (show message).

**Postconditions:** Report displayed/exportable.

**Failed Condition:** Error shown.
