[//]: # ' Copyright (c) 2025 Oracle and/or its affiliates.                         '
[//]: # '                                                                          '
[//]: # ' Licensed under the Apache License, Version 2.0 (the "License")           '
[//]: # ' you may not use this file except in compliance with the License.         '
[//]: # ' You may obtain a copy of the License at                                  '
[//]: # '                                                                          '
[//]: # '     http://www.apache.org/licenses/LICENSE-2.0                           '
[//]: # '                                                                          '
[//]: # ' Unless required by applicable law or agreed to in writing, software      '
[//]: # ' distributed under the License is distributed on an "AS IS" BASIS,        '
[//]: # ' WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. '
[//]: # ' See the License for the specific language governing permissions and      '
[//]: # ' limitations under the License.                                           '

# EclipseLink bug test

Simple application to reproduce EclipseLink bug based on _Docker_, _Testcontainers for Java_
and _EclipseLink_.

## Build

Requires EclipseLink master branch build locally because of EclipseLink 5.0.0-SNAPSHOT dependency.

## Usage

To run the test, make sure you have Docker running and simply run maven build:

```
    mvn clean verify
```
