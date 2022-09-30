# Western Music Notation for Java (wmn4j)

![pull_request](https://github.com/otsob/wmn4j/actions/workflows/pull_request.yaml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

wmn4j is a Java library for handling western music notation. The main purpose of wmn4j is to enable easy and efficient
analysis of scores and wmn4j is intended to also provide functionality for algorithmic composition by generating scores.

## The structure of wmn4j

The [API Documentation](https://otsob.github.io/wmn4j) provides the Javadoc for the latest release of wmn4j. There are a
few architectural design principles in wmn4j:

* All notation classes are immutable
* Builder classes are used for creating objects
* Scores can be read using different types of iterators

See the [examples](./doc/examples) directory for simple examples of how to use wmn4j.

### Building wmn4j

wmn4j is developed and built with OpenJDK 17. wmn4j uses Gradle and can be built by running Gradle build. With the
current configuration the build consists of compilation, unit tests, and static analysis. It is recommended to delegate
the building of the project to Gradle using the provided Gradle wrapper in the IDE to ensure all dependencies etc. are
handled correctly.

## Contributing

Contributions to wmn4j are very welcome. If you are interested, feel free to message me (otsob) through Github.
Contributing to wmn4j happens throught pull requests. For commits we try to follow basic practices (for example, see the
ones by [Painsi](https://gist.github.com/robertpainsi/b632364184e70900af4ab688decf6f53)
or [Beams](https://chris.beams.io/posts/git-commit/)).

As wmn4j is a library, the testing happens through unit tests and it is preferred that changes come accompanied by
corresponding unit tests.

The coding style of wmn4j is not very strict, but there are some [basic guidelines](CODING_CONVENTIONS.md).

Contributing to this project is supposed to be enjoyable, so behaving nicely and respectfully is appreciated. For a
general guideline on code-of-conduct we follow the one outlined
by [Contributor Covenant](https://www.contributor-covenant.org/version/1/4/code-of-conduct).

For pull requests target the `development` branch. The `master` branch is reserved for releases.

## Built With

* [Gradle](https://gradle.org)
* [Apache Commons Math](https://commons.apache.org/proper/commons-math/)
* [SLF4J](http://www.slf4j.org)

## Versioning

Wmn4j is still in the alpha phase. The released versions of wmn4j are available
on [Maven central](https://search.maven.org/artifact/org.wmn4j/wmn4j).

## Authors

* **Otso Björklund** - [otsob](https://github.com/otsob)
* **Matias Wargelin**

See also the list of [contributors](https://github.com/otsob/wmn4j/graphs/contributors) who participated in this
project.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details
