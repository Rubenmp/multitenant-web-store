#!/bin/bash

pdflatex dissertation.tex
bibtex dissertation.tex
pdflatex dissertation.tex

