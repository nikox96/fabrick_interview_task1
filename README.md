# fabrick_task1

## Fabrick Interview Exercise 1 - Asteroid Path API
The goal of this task is to code a REST API project that expose an API that, given an asteroid, it describes its path across
the Solar System.

Here is an example of the API we want to expose:

`GET /api/fabrick/v1.0/asteroids/{asteroidId}/paths?fromDate={fromDate?now-100years}&toDate={toDate?now}
`

`[
{
"fromPlanet": "Juptr",
"toPlanet": "Earth",
"fromDate": "YYYY-MM-DD",
"toDate": "YYYY-MM-DD"
},
{
"fromPlanet": "Earth",
"toPlanet": "Venus",
"fromDate": "YYYY-MM-DD",
"toDate": "YYYY-MM-DD"
},
...
]`

You can rely on the official NASA Open API; have a look at the Asteroids - NeoWs service, in particular at the Neo -
Lookup API.

## Constraints
The query parameters fromDate and toDate are optional, by default:

`fromDate=now-100years
toDate=now`

Each date value should have the format YYYY-MM-DD
asteroidId is the Asteroid SPK-ID correlates to the NASA JPL small body
For each tuple (fromPlanet, toPlanet), fromPlanet?toPlanet
Only paths from fromDate up to toDate should be displayed
fromDate should be the very first day related to the passage from fromPlanet, while toDate should be the very first
day related to the passage from toPlanet (have a look at close_approach_date); so for example if we have:

`[
{
"close_approach_date": "1917-04-30",
"orbiting_body": "Juptr"
},
{
"close_approach_date": "1920-05-01",
"orbiting_body": "Juptr"
},
{
"close_approach_date": "1930-06-01",
"orbiting_body": "Earth"
},
{
"close_approach_date": "1937-02-04",
"orbiting_body": "Earth"
},
{
"close_approach_date": "1950-08-07",
"orbiting_body": "Juptr"
},
{
"close_approach_date": "1991-06-22",
"orbiting_body": "Juptr"
}
]`

The output should be:

`[
{
"fromPlanet": "Juptr",
"toPlanet": "Earth",
"fromDate": "1917-04-30",
"toDate": "1930-06-01"
},
{
"fromPlanet": "Earth",
"toPlanet": "Juptr",
"fromDate": "1930-06-01",
"toDate": "1950-08-07"
}
]`