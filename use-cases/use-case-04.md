# Use Case 04 — Produce Salary Report (By Role)

**Goal in Context:** As an HR advisor I want to produce a report on the salary of employees of a given role so that I can support financial reporting of the organisation.

**Scope:** HR System

**Primary Actor:** HR Advisor

**Preconditions:**
- The database contains `titles` and `salaries` tables and current entries (`to_date = '9999-01-01'`).
- System connected to DB.

**Trigger:** HR picks a role (title) and chooses "Salary by Role".

**Main Success Scenario:**
1. HR opens "Salary by Role".
2. System prompts HR to enter or select title.
3. HR enters/selects the title.
4. System runs query filtering `titles.title = <chosen> and to_date = '9999-01-01'` and `salaries.to_date = '9999-01-01'`.
5. System returns list of employees with salary for that title, sorted by emp_no.
6. System displays result and allows export/print.

**Extensions:** No employees with that title -> show empty message; DB error -> show message and allow retry.

**Success Condition:** List displayed.

**Failed Condition:** Report not produced.
