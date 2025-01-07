@echo off
:retry
@echo on
ssh -o ConnectTimeout=1 %1
@echo off
if %errorlevel% neq 0 (
    goto retry
)
@echo on