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

```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

##### Example Code
- Kaggle Titanic: https://github.com/twiddles/jftrl/blob/master/src/test/java/org/jftrl/examples/KaggleTitanic.java


