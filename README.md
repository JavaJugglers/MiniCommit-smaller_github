## Overview

MiniCommit is a version-control system designed to mimic some of the basic functionalities of the popular Git system, albeit in a more simplified manner. This project aims to provide a fundamental understanding of how version-control systems work and their importance in managing complex or collaborative coding projects.

### Key Features

1. **Committing**: Save the contents of entire directories of files, referred to as *commits*.
2. **Checking Out**: Restore a version of one or more files or entire commits.
3. **Log**: View the history of your commits.
4. **Branches**: Maintain related sequences of commits, called *branches*.
5. **Merging**: Merge changes made in one branch into another.

### Purpose

MiniCommit assists in saving versions of a project periodically, allowing recovery to a previously committed version if needed. It is particularly useful in complex projects or collaborations, ensuring that changes are not lost and can be integrated smoothly.

## Concepts

- **Commit**: A snapshot of your entire project at a particular point in time.
- **Linked List**: The commits are visualized as a linked list, where each commit contains a reference to its parent commit.
- **Head Pointer**: Keeps track of the current commit in the linked list.
- **Commit Tree**: Visualization of the history of different versions of files, resembling a tree structure.
- **Immutable Commit Trees**: Once created, a commit node cannot be destroyed or changed.

## Internal Structures

MiniCommit simplifies Git's structure by incorporating trees into commits and not dealing with subdirectories. It uses SHA-1 cryptographic hash function for unique identification of commits and blobs.

## Overall Specification

- The main class is `gitlet.Main` with a `main` method.
- Commands should follow specific runtime requirements and handle defined failure cases.
- The `.gitlet` directory stores all metadata and old copies of files.

## Commands

1. **init**: Initialize a new MiniCommit version-control system.
2. **add**: Add a file to the staging area.
3. **commit**: Commit changes in the staging area.
4. **rm**: Remove files from the current commit and staging area.
5. **log**: Display information about each commit.
6. **global-log**: Display information about all commits.
7. **find**: Find commits with a particular message.
8. **status**: Show the status of branches and staged files.
9. **checkout**: Checkout files or branches.
10. **branch**: Create a new branch.
11. **rm-branch**: Remove a branch.
12. **reset**: Reset to a specific commit.
13. **merge**: Merge two branches.

## Testing

Includes integration and unit tests. Use `testing/tester.py` for running integration tests.

## Running Commands

Use `java gitlet.Main [command]` to execute any command.

## Important Notes

- Branches and merging form a core functionality of MiniCommit.
- Project requires Java programming language and standard Java libraries.
