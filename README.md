# Shift-rostering

## Task:

Schedule shifts for all employees in a specific group in a period of time

### Constraints:

1. Each shift is assigned to one to many employees who work as required roles
2. Try to distribute the shifts evenly to all employees
3. No employee works more than 1 shift in 1 day
4. No employee works more than 1 shift in 12 hours

### Models:

1. Shift (Input)
2. ShiftAssignment (Planning entity)
3. Employee (Input)
4. Schedule (Planning solution)

### Input:

1. employees: all employees in a specified group
2. shifts: all shifts for scheduling
3. startDate: start date of the first shift
4. endDate: end date of the last shift
5. allRequiredRoles: a set of roles that the shifts to be scheduled require

#### e.g.

```json
{
  "employees": [
    {
      "id": 1,
      "name": "user 1",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 2,
      "name": "user 2",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 3,
      "name": "user 3",
      "roles": [
        "Clean"
      ]
    },
    {
      "id": 4,
      "name": "user 4",
      "roles": [
        "Clean"
      ]
    },
    {
      "id": 5,
      "name": "user 5",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 6,
      "name": "user 6",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 7,
      "name": "user 7",
      "roles": [
        "Dev",
        "Design"
      ]
    },
    {
      "id": 8,
      "name": "user 8",
      "roles": [
        "Dev",
        "Design"
      ]
    }
  ],
  "allRequiredRoles": [
    "Dev",
    "Design",
    "Clean"
  ],
  "shifts": [
    {
      "id": 1,
      "name": "develop new features",
      "startAt": "09:00",
      "endAt": "12:00",
      "requiredRoles": {
        "Dev": 2
      }
    },
    {
      "id": 2,
      "name": "clean up",
      "startAt": "15:00",
      "endAt": "17:00",
      "requiredRoles": {
        "Clean": 1
      }
    },
    {
      "id": 3,
      "name": "develop new features",
      "startAt": "20:00",
      "endAt": "23:00",
      "requiredRoles": {
        "Design": 2
      }
    }
  ],
  "startDate": "2022-11-21",
  "endDate": "2022-11-26"
}
```

### Output:

A list containning all the scheduled shift assignments

```text
List<ShiftAssignment> shiftAssignments;
```

#### e.g.

<img width="1336" alt="Screen Shot 2022-09-19 at 9 39 53 PM" src="https://user-images.githubusercontent.com/46456200/191148679-840716ce-26e1-4ac4-a57d-684a002fa763.png">

