@echo off

call ..\..\tools\build\set_JAVA_HOME
set ANT_HOME=..\..\tools\build\ant
set PATH=..\..\tools\build\ant\bin;%PATH%

ant -Dclassname=kz.gamma.webra.services.client.test.TestWebraWSClientSch