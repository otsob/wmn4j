# Western Music Notation for Java (wmn4j)

[![Build Status](https://travis-ci.com/otsob/wmn4j.svg?branch=master)](https://travis-ci.com/otsob/wmn4j)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

wmn4j is a Java API for handling western music notation.
The main purpose of wmn4j is to enable easy and efficient analysis of scores and wmn4j is intended to also provide funtionality for algorithmic composition by generating scores.

## The structure of wmn4j

The [API Documentation](https://otsob.github.io/wmn4j) provides a comprehensive overview of all classes and interfaces in wmn4j. There are a few architectural design principles in wmn4j:
* All notation classes are immutable
* Builder classes are used for creating objects
* Scores can be read using different types of iterators

### Building wmn4j

wmn4j uses Gradle and can be built by running Gradle build. With the current configuration the build consists of compilation, unit tests, and static analysis.

## Contributing

Contributions to wmn4j are very welcome. If you are interested, feel free to message me (otsob) through Github.
Contributing to wmn4j happens throught pull requests. For commits we try to follow basic practices (for example, see the ones by [Painsi](https://gist.github.com/robertpainsi/b632364184e70900af4ab688decf6f53) or [Beams](https://chris.beams.io/posts/git-commit/)).

As wmn4j is an API, the testing happens through unit tests and it is preferred that changes come accompanied by corresponding unit tests.

The coding style of wmn4j is not very strict, but there are some [basic guidelines](CODING_CONVENTIONS.md).

Contributing to this project is supposed to be enjoyable, so behaving nicely and respectfully is appreciated. For a general guideline on code-of-conduct we follow the one outlined by [Contributor Covenant](https://www.contributor-covenant.org/version/1/4/code-of-conduct).


## Built With

* [Gradle](https://gradle.org) - Dependency Management


## Versioning

There is currently no released version of wmn4j. The initial beta 0.1 is in the works currently.


## Authors

* **Otso Björklund** - *Initial work* - [otsob](https://github.com/otsob)

See also the list of [contributors](https://github.com/otsob/wmn4j/graphs/contributors) who participated in this project.


## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details
