# Shift-employee rostering

## Task:
Schedule shifts for all employees in department a, location b from 2022-11-21 to 2022-11-25.
Each day has 4 shifts: 9~12, 12~15, 15~18. 18~23.

## Requirements:

1. Each shift is assigned to a single employee ✅
2. Try to distribute the shifts evenly to employees ✅
3. No employee works more than 1 shift in 1 day ✅
4. No employee works more than 1 shift in 12 hours ✅

### Models:

1. Shift
2. Shift Assignment
3. Employee

### Explanation:

1. One shift entity can be used to create multiple shiftAssignment entities.
2. Multiple shiftAssignment entity can be assigned to one employee.
3. The number of employees per shift is known. Thus, Employee should be a planning variable.



