Modelbuilder
============

Modelbuilder is a computational geometry library for Processing designed to help with parametric and generative modeling, while eliminitating complex and tedious tasks whenever possible. 

Modelbuilder provides a set of object-oriented data structures and workflow metaphors (UVertexList define edges, UGeometry is used to generate and modify polygon meshes). The logic used is the familiar beginShape() / endShape() mechanism, optimized and abstracted to eliminate the need for tedious iteration through lists of vertices and faces etc.  

Modelbuilder was first released in 2011 as part of my artist-in-residence project with Makerbot Industries, and the library is thus somewhat biased towards digital fabrication (3D printing, laser cutting etc.) My design priority has always been ease of coding rather than maximum realtime performance, but the library still has plenty of useful tools for realtime applications. 

**Users of my old library [unlekkerLib](http://workshop.evolutionzone.com/unlekkerlib/)** should note that Modelbuilder replaces that library. Having both libraries installed will produce unresolvable conflicts. Modelbuilder does offers most of the same functionality, although with significant API changes. If there was something in unlekkerLib you need I suggest you open an issue on Modelbuilder and I will try to accommodate you.

Download + info about Processing 2.0 beta:
======================

A compiled Modelbuilder for Processing 2.0b7 can be downloaded here: [modelbuilder v0019](https://github.com/mariuswatz/modelbuilder/blob/master/exported/modelbuilder0019-2.0b7.zip). It requires [Control-P5 2.0.4](http://www.sojamo.de/libraries/controlP5/) for all examples that include GUI elements.

The code base in [src](https://github.com/mariuswatz/modelbuilder/tree/master/src) is Processing 2.0b7 compatible,  It has not been tested extensively on Processing 2.0b8, but should work fine. 

I am having some issues using 2.0 for my own projects, so I will maintain code for 1.5.1 for a while longer. See [src-0151-compatible](https://github.com/mariuswatz/modelbuilder/tree/master/src-0151-compatible). Ideally I'd like to clean up some inconsistencies in the Modelbuilder API for a final 2.0 release, time permitting.

Background: Modelbuilder as Swiss Army Knife
======================
Modelbuilder was intended to be limited in scope at its inception (i.e. 3D printing), but has been expanded through my use of it as a valuable teaching tool for [ongoing series of workshops](http://workshop.evolutionzone.com/workshops-in-new-york/). Consequently, the library has expanded in scope and predictably has become the code equivalent of a Swiss Army Knife, containing tools I find useful but hopefully avoiding outright feature bloat.

Some examples of secondary functions:

- Color palette generation (UColorTool)
- File saving and parsing (UIO, UDataText, UFileNode and ULogUtil)
- FFT code with automatic damping and peak following for use in animation and sound-responsive applications.

All major functions are demonstrated in the provided examples, however there are some potential helpful tools hiding in the shadows. Some experimental features in various states of polish. These are recommended only for intrepid users with the ability to read code. I welcome input on useful additions, bugs and blatant stupidity.

With the final jump to Processing 2.0 I hope to do some much needed house-cleaning, making the code a little more consistent and changing some of the underlying design. This will likely come at the cost of backwards compatibility. Eggs, omelettes, and so on.

[Marius Watz, Feb 2013](http://www.mariuswatz.com/)
