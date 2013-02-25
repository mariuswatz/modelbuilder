Modelbuilder
============

Modelbuilder is a computational geometry library for Processing designed to help with parametric and generative modeling, while eliminitating complex and tedious tasks whenever possible. 

Modelbuilder provides a set of object-oriented data structures and workflow metaphors (UVertexList define edges, UGeometry is used to generate and modify polygon meshes). The logic used is the familiar beginShape() / endShape() mechanism, optimized and abstracted to eliminate the need for tedious iteration through lists of vertices and faces etc.  

Modelbuilder was first released in 2011 as part of my artist-in-residence project with Makerbot Industries, and the library is thus somewhat biased towards digital fabrication (3D printing, laser cutting etc.) My design priority has always been ease of coding rather than maximum realtime performance, but the library still has plenty of useful tools for realtime applications. 

Update for Processing 2.0 beta series: [modelbuilder v0019](https://github.com/mariuswatz/modelbuilder/blob/master/exported/modelbuilder0019-2.0b7.zip) is compatible with Processing 2.0b7, but has not been tested on Processing 2.0b8.

Modelbuilder as Swiss Army Knife
======================
Somewhat limited in scope at its inception (i.e. 3D printing,) the library has since proven valuable as a teaching tool, especially for the shorter [independent workshops](http://workshop.evolutionzone.com/workshops-in-new-york/) I teach in New York. Consequently Modelbuilder has expanded in scope and become the code equivalent of a Swiss Army Knife, containing tools I find useful but hopefully avoiding outright feature bloat.

Some examples of secondary functions:

- Color palette generation (UColorTool)
- File saving and parsing (UIO, UDataText, UFileNode and ULogUtil)
- FFT code with automatic damping and peak following for use in animation and sound-responsive applications.

With the final jump to Processing 2.0 I plan to do some much needed house-cleaning, making the code a little more consistent and changing some of the underlying design. This will likely come at the cost of some loss of backwards compatibility. Eggs and omelettes, etc.

[Marius Watz, Feb 2013](http://www.mariuswatz.com/)
