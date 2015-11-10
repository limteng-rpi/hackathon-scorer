# Scorer
## How to use
Run the scorer by executing:
```
java -jar eval.jar [annotation file 1: gold file] [annotation file 2] ([option 1] [option 2] ...)
```
options:
  - `-t, --textMatchMode`: ignore offsets. Tokens with identical text in a document are regarded as the same one and counted only once.

## Compile from source code
If you'd like to compile form source code, it's not necessary to build a jar.
```
cd src
javac work/Evaluation.java
java work/Evaluation [file1] [file2] ([option 1] [option 2] ...)
```

## Sample input files
Sample input files are under `data/` directory.
