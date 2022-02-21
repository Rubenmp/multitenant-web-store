#!/bin/bash

pdflatex dissertation.tex
biber dissertation
pdflatex dissertation.tex
pdflatex dissertation.tex

