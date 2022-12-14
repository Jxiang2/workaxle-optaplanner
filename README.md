# Workaxle-optaplanner

## Task:

Schedule shifts for all employees in a specific group in a period of time.

e.g. A department manager want to automatically schedule shifts for all of his employees who are
eligible to take one or many of those shifts.

### Constraints:

Base constraints

1. Each shift is assigned to one or many employees who work as required roles ✅
2. Try to distribute the shifts evenly to all employees ✅
3. No employee works more than 1 shift in 1 day ✅
4. No employee works on shifts that overlap in time ✅

Optional constraints

1. No employee works more than 1 shift in N hours (0 by default) ✅
2. No employee works on weekends (true by default) ✅
3. Each employee should work no more than X hours during a Y-day period ✅

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
5. settings: global setting to enable/disable optional constraints

#### e.g.

```json
{
  "employees": [
    {
      "id": 74,
      "name": "Database Design",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 88,
      "name": "Java Dev",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 99,
      "name": "Frontend Design",
      "roles": [
        "Design"
      ]
    },
    {
      "id": 101,
      "name": "Frontend Dev",
      "roles": [
        "Dev"
      ]
    },
    {
      "id": 43,
      "name": "First Cleaner",
      "roles": [
        "Clean"
      ]
    },
    {
      "id": 84,
      "name": "Second Cleaner",
      "roles": [
        "Clean",
        "Security"
      ]
    },
    {
      "id": 59,
      "name": "Team Leader 1",
      "roles": [
        "Dev",
        "Design"
      ]
    },
    {
      "id": 65,
      "name": "Team Leader 2",
      "roles": [
        "Dev",
        "Design"
      ]
    }
  ],
  "shifts": [
    {
      "id": 108,
      "name": "design new features",
      "startAt": "09:00",
      "endAt": "12:00",
      "requiredRoles": {
        "Design": 2,
        "Dev": 1
      }
    },
    {
      "id": 73,
      "name": "clean up",
      "startAt": "15:00",
      "endAt": "17:00",
      "requiredRoles": {
        "Clean": 1
      }
    },
    {
      "id": 90,
      "name": "develop new features",
      "startAt": "20:00",
      "endAt": "23:00",
      "requiredRoles": {
        "Dev": 2,
        "Design": 1
      }
    }
  ],
  "startDate": "2022-11-21",
  "endDate": "2022-11-25",
  "settings": {
    "hoursBetweenShifts": 12,
    "maxHoursOfWork": 17,
    "allowWeekendShifts": false
  }
}
```

more example inputs can be found at src/main/org/workaxle/bootstrap/examples

### Output:

A list containing all the scheduled shift assignments

```text
List<ShiftAssignment> shiftAssignments;
```

#### e.g.

```json
[
  {
    "id": 1,
    "role": "Dev",
    "shift": {
      "shiftId": 108,
      "name": "develop new feature",
      "startAt": "09:00",
      "endAt": "12:00",
      "requiredRoles": {
        "Dev": 2,
        "Design": 1
      }
    },
    "employee": {
      "employeeId": 65,
      "name": "employee 1",
      "roles": [
        "Dev",
        "Design"
      ]
    },
    "conflicts": {
      "atLeastNHoursBetweenTwoShifts": [
        2,
        7
      ],
      "atMostOneShiftPerDay": [
        7
      ],
      "onlyRequiredRole": []
    }
  },
  "..."
]
```
