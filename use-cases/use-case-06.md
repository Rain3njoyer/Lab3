# Use Case 06 — View Employee Details

**Goal in Context:** As an HR advisor I want to view an employee's details so that the employee's promotion request can be supported.

**Primary Actor:** HR Advisor

**Preconditions:**
- Database available.
- Employee exists.

**Trigger:** HR searches for employee by ID or name and selects record.

**Main Success Scenario:**
1. HR searches and selects employee.
2. System fetches employee personal info, current title, salary, department and manager.
3. System displays details.

**Extensions:** Not found -> display not found; DB error -> show message.

**Success Condition:** Employee details displayed.

**Failed Condition:** Details not shown.
