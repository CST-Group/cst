[![Build Status](https://travis-ci.org/CST-Group/cst.svg?branch=master)](https://travis-ci.org/CST-Group/cst)
[![Maintainability](https://api.codeclimate.com/v1/badges/e9d016cbb9689600abb7/maintainability)](https://codeclimate.com/github/CST-Group/cst/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e9d016cbb9689600abb7/test_coverage)](https://codeclimate.com/github/CST-Group/cst/test_coverage)
[![](https://jitpack.io/v/CST-Group/cst.svg?label=Release)](https://jitpack.io/#CST-Group/cst)


# Welcome to the CST Toolkit pages.

The [CST Toolkit](http://cst.fee.unicamp.br) is a Java-based toolkit to allow the construction of Cognitive Architectures. It has been developed at the [University of Campinas](http://www.dca.fee.unicamp.br) by a group of researchers in the field of Cognitive Architectures leaded by Prof. [Ricardo Gudwin](http://faculty.dca.fee.unicamp.br/gudwin). 

Note: This library is still under development, and some concepts or features might not be available yet. [Feedback/bug report](https://github.com/CST-Group/cst/issues) and [Pull Requests](https://github.com/CST-Group/cst/pulls) are most welcome!

## Installation

### Gradle

- Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```
	repositories {
			...
			maven { url 'https://jitpack.io' }
	}
```

- Step 2. Add the dependency

```
	dependencies {
            ...
            implementation 'com.github.CST-Group:cst:0.3.0'
	}
```

Sometimes, the version number (tag) in this README gets out of date, as maintainers might forget to change it when releasing. Always check the release badge [![](https://jitpack.io/v/CST-Group/cst.svg?label=Release)](https://jitpack.io/#CST-Group/cst) to see the actual current version number that should be provided in the dependencies in Step 2 above.

### Maven

- Step 1. Add the JitPack repository to your build file.

```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

- Step 2. Add the dependency

```
	<dependency>
	    <groupId>com.github.CST-Group</groupId>
	    <artifactId>cst</artifactId>
	    <version>0.3.0</version>
	</dependency>
```

Sometimes, the version number (tag) in this README gets out of date, as maintainers might forget to change it when releasing. Always check the release badge [![](https://jitpack.io/v/CST-Group/cst.svg?label=Release)](https://jitpack.io/#CST-Group/cst) to see the actual current version number that should be provided in the dependencies in Step 2 above.

### Manual

Download the latest [release](https://github.com/CST-Group/cst/releases) and set it as a dependency in your project.

## Building the source code

This release uses gradle to download the dependencies from MavenCentral. It does not require you to have gradle installed in your system because it uses the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). Depending on your operational system, you might execute the gradlew script or the gradlew.bat script in order to compile the code. You might need the JDK to be properly installed in order to build the code. You should call "gradlew <task>" in order to build the code. Available tasks can be discovered using "gradlew tasks". After calling "gradlew build", the CST library will be available at build/libs directory.

## Changelog / Migrations

Follow the [release](https://github.com/CST-Group/cst/releases) page to better understand the breaking changes of new versions.

## Example

To get started, you can take a look at our [Traffic Signal Control Application](https://github.com/CST-Group/traffic-signal-control-app) and our [application that controls intelligent agents in World Server 3D](https://github.com/CST-Group/MotivationalSystemWithWorldServer3D) (soon we will provide more examples in the library repository).

## Cognitive Architectures
Cognitive Architectures are general-purpose control systems' architectures inspired by scientific theories developed to explain cognition in animals and men. Cognitive Architectures have been employed in many different kinds of applications, since the control of robots to decision-making processes in intelligent agents. Usually, a cognitive architecture is decomposed based on its cognitive capabilities, like perception, attention, memory, reasoning, learning, behavior generation, etc. 
Cognitive Architectures are, at the same time, theoretical modelings for how many different cognitive processes interact to each other in order to sense, reason and act, and also a software framework which can be reused through different applications. The most popular cognitive architectures usually have their code available at the Internet (with different kinds of licenses), such that different researchers are able to download this code and make experimentations with these architectures. 

## Origins of the CST Toolkit
The CST toolkit has been used successfully in a number of MS and PhD thesis in our group, either for building a cognitive architecture to be used in a particular experiment or also to foster further improvements in the own CST code due to the applications requirements.

## Basic Notions
Figure 1 illustrates the core of the CST toolkit. The basic notion, which is used in a widespread way within the cognitive architecture is the notion of a codelet. Codelets are small pieces of non-blocking code, each of them executing a well defined and simple task. The idea of a codelet is of a piece of code which ideally shall be executed continuously and cyclically, time after time, being responsible for the behavior of a system's independent component running in parallel. The notion of codelet was introduced originally by Hofstadter and Mitchell (1994) and further enhanced by Franklin (1998). The CST architecture is codelet oriented, since all main cognitive functions are implemented as codelets. This means that from a conceptual point of view, any CST-implemented system is a fully parallel asynchronous multi-agent system, where each agent is modeled by a codelet. CST's codelets are implemented much in the same manner as in the [LIDA cognitive architecture](http://ccrg.cs.memphis.edu/framework.html) and largely correspond to the special-purpose processes described in Baar's Global Workspace Theory (Baars & Franklin 2007). Nevertheless, for the system to work, a kind of coordination must exist among codelets, forming coalitions which by means of a coordinated interaction, are able to implement the cognitive functions ascribed to the architecture. This coordination constraint imposes special restrictions while implementing codelets in a serial computer. In a real parallel system, a codelet would simply be called in a loop, being responsible to implement the behavior of a parallel component. In a serial system like a computer, the overall system might have to share the CPU with its many components. In time-sharing systems, the concepts of process and thread could be used to implement systems with requirements like this. Nevertheless, in a cognitive system, there might be some subtleties, which might make an implementation with real threads an unsuitable one. Using real threads, in a context of a large number of threads, situations where some thread is called many times while other threads are called none or just a few times might appear, due to optimization constraints. In a codelet-based cognitive architecture, there might be a very large number of codelets, each of them responsible for the implementation of a small part of a coordinated system of components. Some of these codelets might be critical for the system performance, in terms of time restrictions. Other codelets might have more relaxed conditions. In a serial system, where it might be impossible to run all the codelets in parallel, a scheduling mechanism should be employed to guarantee that more important codelets are called more frequently, taking the preference from not-so-critical codelets. Another constraint is that usually, there might be a predefined order in which the codelets should be called, generated by the coordination constraint (e.g. perception codelets should be called before reasoning codelets, because reasoning codelets need the outputs of perception codelets as its inputs). 

![CST Core](http://faculty.dca.fee.unicamp.br/gudwin/sites/faculty.dca.fee.unicamp.br.gudwin/files/cst/CogSys-Core.png)

Figure 1 - The CST Core

A codelet has two main inputs (which are characterized as In and B in the figure), a local input (In) and a global input (B). The local input is used for the codelet to get information from memory objects, which are available at the Raw Memory. The global input is used for the codelet to get information from the global workspace mechanism (Baars & Franklin 2007). The information coming from the global workspace is variable at each instant of time, and usually is related to a summary, an executive filter which select the most relevant piece of information available in memory at each timestep. The two outputs of a codelet are a standard output, which is used to change or create new information in the Raw Memory, and the value level, which indicates the relevance of the information provided at the output, and is used by the Global Workspace mechanism in order to select information to be destined to the global workspace. 
Using this Core, the CST toolkit provides different kinds of codelets to perform most of the cognitive functions available at a cognitive architecture, as indicated in figure 2. Also, memory objects are scattered among many different kinds of memories. The Raw Memory is so split into many different memory systems, which are used to store and access different kinds of knowledge.  Using the available codelets, different cognitive architectures, using different strategies for perception, attention, learning, planning and behavior generation can be composed in order to perform the role necessary to address a specific control problem. These codelets are constructed according to different techniques in intelligent systems, like neural networks, fuzzy systems, evolutionary computation, rule-based systems, Bayesian networks, etc., which are integrated into a whole control and monitoring system. 
The definition and choice of a particular cognitive architecture is constructed using a composition of different kinds of codelets, according to the control problem under analysis. Depending on the problem to be addressed, different strategies might be necessary or useful, depending on the problem constraints. 

![CST Overall Architecture](http://faculty.dca.fee.unicamp.br/gudwin/sites/faculty.dca.fee.unicamp.br.gudwin/files/cst/CogSys-Codelets.png)

Figure 2 - The CST Overall Architecture: Codelets

## Publications

Refer to  CST's publications to better understand the concepts behind the implemented code structures:

- [The cognitive systems toolkit and the CST reference cognitive architecture](https://doi.org/10.1016/j.bica.2016.07.005);
- [A machine consciousness approach to urban traffic control](https://doi.org/10.1016/j.bica.2015.10.001).

## Requirements

CST requires at minimum Java 8.

### Authors and Contributors
The main contributors of this project are: 
* [Ricardo Ribeiro Gudwin](https://github.com/rgudwin)
* [Klaus Raizer](https://github.com/KRaizer)
* [André Luis Ogando Paraense](https://github.com/andre-paraense)
* [Suelen Mapa de Paula](https://github.com/suelenmapa)
* Vera Aparecida de Figueiredo
* [Elisa Calhau de Castro](https://github.com/ecalhau)
* [Eduardo de Moraes Fróes](https://github.com/eduardofroes)
* [Wandemberg Santana Pharaoh Gibaut](https://github.com/wandergibaut)

License
--------

    Copyright 2016 CST-Group

    Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.gnu.org/licenses/lgpl-3.0.html

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
    
