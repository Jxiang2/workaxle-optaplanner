# Shift-employeeGroup rostering

## Task:

Schedule shifts for all employees in a department

## Requirements:

1. Each shift is assigned to 2 employees
2. Try to distribute the shifts evenly to employees ✅
3. No employeeGroup works more than 1 shift in 1 day ✅
4. No employeeGroup works more than 1 shift in 12 hours ✅

### Models:

1. Shift
2. Shift Assignment
3. Employee

### Explanation:

1. One shift entity can be used to create multiple shiftAssignment entities.
2. Multiple shiftAssignment entity can be assigned to one employeeGroup.
3. The number of employees per shift is known. Thus, Employee should be a planning variable.



