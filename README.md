**This repo is for archival purposes only.** See [https://github.com/mariuswatz/modelbuilderMk2](https://github.com/mariuswatz/modelbuilderMk2) for recent versions of ModelbuilderMk2 for Processing 2.x. You can download Processing-ready builds of ModelbuilderMk2 from [the export folder](https://github.com/mariuswatz/modelbuilderMk2/tree/master/export) of the new repo.


_Note: The old Modelbuilder has been superceded by ModelbuilderMk2, which is a complete rewrite based on the core ideas of the old library. ModelbuilderMk2 gets rid of many inconsistencies in the original library design, as well as misplaced feature bloat. Unfortunately, ModelbuilderMk2 is not backwards compatible._ 




Modelbuilder
============

Modelbuilder is a computational geometry library for Processing designed to help with parametric and generative modeling, while eliminitating complex and tedious tasks whenever possible. 

Modelbuilder provides a set of object-oriented data structures and workflow metaphors (UVertexList define edges, UGeometry is used to generate and modify polygon meshes). The logic used is the familiar beginShape() / endShape() mechanism, optimized and abstracted to eliminate the need for tedious iteration through lists of vertices and faces etc.  

Modelbuilder was first released in 2011 as part of my artist-in-residence project with Makerbot Industries, and the library is thus somewhat biased towards digital fabrication (3D printing, laser cutting etc.) My design priority has always been ease of coding rather than maximum realtime performance, but the library still has plenty of useful tools for realtime applications. 

Update June 13, 2013
======================

Fun news: I've posted a rough pre-beta port of Modelbuilder to JavaScript. Behold [https://github.com/mariuswatz/modelbuilderjs](Modelbuilder.js), incomplete and quite likely buggy, but with the most basic geometry concepts in place. A sample sketch can be seen here: [http://workshop.evolutionzone.com/modelbuilderjs/](http://workshop.evolutionzone.com/modelbuilderjs/).

Like its Java sibling, Modelbuilder.js is the feeble attempts of a mathematically challenged coder to simplify computational geometry tasks, minimize repetitive code (esp. for-loops) and empower non-experts. The code is pure JS and tries to follow established JS conventions. It is also free of dependencies on other libraries, although I plan to add convenience methods to facilitate integration with [Processing.js](http://processingjs.org/) and (possibly) [Three.js](http://threejs.org/).

Update March 16, 2013
======================

- Some minor code fixes
- Separated code specific to Processing 1.5.1 and Processing 2.0b series to separate directories for convenience. "src-universal" contains code that works with both code bases. I still use
1.5.1 for production
- New sub-library: UMovieMaker, a hack replacement for Shiffman's MovieMaker that will work with Processing 2.0b. The library uses uses the Monte Media Library (http://www.randelshofer.ch/monte/) by Werner Randelshofer and can write Quicktime without being dependent on QTJava.zip. See included examples.

Modelbuilder-0020 has been exported based on this code and briefly tested with Processing 2.0b8. 

Downloads: [Modelbuilder-0020](http://workshop.evolutionzone.com/codedist/Modelbuilder0020-2.0b8.zip) / 
[UMovieMaker-001](http://workshop.evolutionzone.com/codedist/UMovieMaker-001.zip)


Modelbuilder as Swiss Army Knife
======================
Somewhat limited in scope at its inception (i.e. 3D printing,) the library has since proven valuable as a teaching tool, especially for the shorter [independent workshops](http://workshop.evolutionzone.com/workshops-in-new-york/) I teach in New York. Consequently Modelbuilder has expanded in scope and become the code equivalent of a Swiss Army Knife, containing tools I find useful but hopefully avoiding outright feature bloat.

Some examples of secondary functions:

- Color palette generation (UColorTool)
- File saving and parsing (UIO, UDataText, UFileNode and ULogUtil)
- FFT code with automatic damping and peak following for use in animation and sound-responsive applications.

With the final jump to Processing 2.0 I plan to do some much needed house-cleaning, making the code a little more consistent and changing some of the underlying design. This will likely come at the cost of some loss of backwards compatibility. Eggs and omelettes, etc.

[Marius Watz, Feb 2013](http://www.mariuswatz.com/)
