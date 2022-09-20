# Shift-employeeGroup rostering

## Task:

Schedule shifts for all employees in a specific group

## Requirements:

1. Each shift is assigned to 1 Dev role and 1 Design role
2. Try to distribute the shifts evenly to all employees
3. No employeeGroup works more than 1 shift in 1 day
4. No employeeGroup works more than 1 shift in 12 hours

### Models:

1. Shift
2. ShiftAssignment (Planning entity)
3. EmployeeGroup
4. Employee
5. Schedule (Planning solution)

### Input:

1. employees: all employees in a specified group
2. roles: all roles required to fulfill the shifts
3. shifts: all shifts for scheduling
4. startDate: start date of the first shift
5. endDate: end date of the last shift

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
        "Design"
      ]
    },
    {
      "id": 3,
      "name": "user 3",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 4,
      "name": "user 4",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 5,
      "name": "user 5",
      "roles": [
        "Clean"
      ]
    },
    {
      "id": 6,
      "name": "user 6",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 7,
      "name": "user 7",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 8,
      "name": "user 8",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 9,
      "name": "user 9",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 10,
      "name": "user 10",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 11,
      "name": "user 11",
      "roles": [
        "Design",
        "Dev",
        "???"
      ]
    },
    {
      "id": 12,
      "name": "user 12",
      "roles": [
        "???",
        " ? "
      ]
    }
  ],
  "roles": [
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
        "Dev": 1,
        "Design": 1
      }
    },
    {
      "id": 2,
      "name": "develop new features",
      "startAt": "12:00",
      "endAt": "15:00",
      "requiredRoles": {
        "Dev": 1,
        "Design": 1
      }
    },
    {
      "id": 3,
      "name": "develop new features",
      "startAt": "15:00",
      "endAt": "18:00",
      "requiredRoles": {
        "Dev": 1,
        "Design": 1
      }
    },
    {
      "id": 4,
      "name": "clean up",
      "startAt": "18:00",
      "endAt": "23:00",
      "requiredRoles": {
        "Clean": 1
      }
    }
  ],
  "startDate": "2022-11-21",
  "endDate": "2022-11-25"
}
```

### Output:
A list containning all the scheduled shift assignments
```java
List<ShiftAssignment>
```

#### e.g.

<img width="1336" alt="Screen Shot 2022-09-19 at 9 39 53 PM" src="https://user-images.githubusercontent.com/46456200/191148679-840716ce-26e1-4ac4-a57d-684a002fa763.png">

