# Use Case 03 — Produce Salary Report (Manager's Department)

**Goal in Context:** As a department manager I want to produce a report on the salary of employees in my department so that I can support financial reporting for my department.

**Scope:** HR System

**Primary Actor:** Department Manager

**Preconditions:**
- Manager authenticated and bound to a department.
- Database accessible.

**Trigger:** Manager selects "Salary Report — My Department".

**Main Success Scenario:**
1. Manager selects action.
2. System identifies manager's department.
3. System queries DB for current salaries for that department.
4. System displays and offers export.

**Extensions:** manager not linked to department (display error), DB error.

**Postconditions:** Report shown.

**Failed Condition:** Error shown.
