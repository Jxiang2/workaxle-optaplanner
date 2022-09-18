# Shift-employee rostering

## Requirements:

1. Every day, each shift is assigned to a single employee ✅
2. Try to distribute the shifts evenly ✅
3. No employee works more than 1 shift in 12 hours ✅

### Models:

1. Shift
2. Shift Assignment
3. Employee

### Explanation:

1. One shift entity can be used to create multiple shiftAssignment entities.
2. Multiple shiftAssignment entity can be assigned to one employee.
3. The number of employees per shift is known. Thus, Employee should be a planning variable.



