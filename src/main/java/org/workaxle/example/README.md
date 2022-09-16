## School Time table example

### Goal:

You will build a command-line application to assign each lesson to a time slot and a room.

```
...
INFO  Solving ended: time spent (5000), best score (0hard/9soft), ...
INFO
INFO  |            | Room A     | Room B     | Room C     |
INFO  |------------|------------|------------|------------|
INFO  | MON 08:30  | English    | Math       |            |
INFO  |            | I. Jones   | A. Turing  |            |
INFO  |            | 9th grade  | 10th grade |            |
INFO  |------------|------------|------------|------------|
INFO  | MON 09:30  | History    | Physics    |            |
INFO  |            | I. Jones   | M. Curie   |            |
INFO  |            | 9th grade  | 10th grade |            |
INFO  |------------|------------|------------|------------|
INFO  | MON 10:30  | History    | Physics    |            |
INFO  |            | I. Jones   | M. Curie   |            |
INFO  |            | 10th grade | 9th grade  |            |
INFO  |------------|------------|------------|------------|
...
INFO  |------------|------------|------------|------------|
```

Your application will assign Lesson instances to Timeslot and Room instances automatically by using AI to adhere to hard
and soft scheduling constraints, for example:

* A room can have at most one lesson at the same time.
* A teacher can teach at most one lesson at the same time.
* A student can attend at most one lesson at the same time.
* A teacher prefers to teach all lessons in the same room.
* A teacher prefers to teach sequential lessons and dislikes gaps between lessons.
* A student dislikes sequential lessons on the same subject.

### Design diagram:

![image](https://www.optaplanner.org/docs/optaplanner/latest/_images/quickstart/school-timetabling/schoolTimetablingClassDiagramPure.png)