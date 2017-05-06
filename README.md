Java-FTRL
=======
An optimized Java implementation of the "Follow the (Proximally) Regularized Leader"-Algorithm.

##### References

- Original paper at: http://goo.gl/iqIaH0
- Loosely based on the Python implementation of: [jeongyoonlee/Kaggler](https://github.com/jeongyoonlee/Kaggler)
- Published under the MIT license (see [LICENSE](https://github.com/twiddles/jftrl/blob/master/LICENSE=))

##### Installation (requires [Maven](http://maven.apache.org/))

- `git clone https://github.com/twiddles/jftrl.git`
- `cd jftrl`
- `mvn clean install`

##### Referenced as a Maven dependency
```
<dependency>
    <groupId>org.ftrl</groupId>
    <artifactId>jftrl</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

##### Example Code
### XOR

```
FTRL clf = new FTRL();
clf.interactions = 2; // automatically consider feature interactions up to  a degree of 2
clf.λ1 = 0.0; // disable regularization for the sake of simplicity

clf.fit("true true", Label.FALSE);
clf.fit("true false", Label.TRUE);
clf.fit("false true", Label.TRUE);
clf.fit("false false", Label.FALSE);

clf.predict("true true"); // Label.FALSE
clf.predict("false true"); // Label.TRUE
```

### Kaggle Titanic
See example code [here](https://github.com/twiddles/jftrl/blob/master/src/test/java/org/jftrl/KaggleTitanicTest.java)


