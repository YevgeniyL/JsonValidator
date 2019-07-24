An application takes two parameters: query string and json array of objects as string. Objects in array have flat structure, fields are not strictly defined (derive from query string). The application should return json array as string of objects matching the query string.

For simplicity we imply that only valid field names are passed to query string.

Sample query string:

* foo=someStringValue // in case of string value use contains logic
* foo=12
* foo>12
* foo<12
* foo>=12
* foo<=12
* foo>12 && bar<12 || baz=12

No parenthesis support needed for logical operations

NB! take into account logical operators precendence: true || false && false || false === true because actually it is the same as true || (false && false) || true, .i.e combination of ANDs is evaluated first.

How to run jumpstart project:

```bash
./gradlew fatJar
java -jar build/libs/poly-test-task-all-1.0-SNAPSHOT.jar "name=Bob" '[{"name":"Bobby","age":25},{"name":"Rob","age":35}]'
```

### Third party libraries

Only json (gson or whatever you like) parsing library is allowed for production code. Also you can use any libraries for testing - it's up to you.

### Non java implementation

If you use other than Java platform it would be good to deliver you solution as docker composable image.