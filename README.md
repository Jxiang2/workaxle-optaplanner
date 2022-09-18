# Shift-employee rostering

### Required models:

1. Shift
2. Shift Assignment
3. Employee

![](../../../Desktop/employeeShiftRosteringModelingGuideB.png)

### Explanation:

1. One shift entity can be used to create multiple shiftAssignment entities.
2. Multiple shiftAssignment entity can be assigned to one employee.
3. The number of employees per shift is known. Thus, Employee should be a planning variable.



