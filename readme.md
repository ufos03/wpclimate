# 🌐 wpCliMate

**Your smart companion for WordPress development with Git and WP-CLI**

---

## 📖 Description

**wpCliMate** is a desktop application built in **Java** that aims to simplify and automate the daily tasks of WordPress developers. It brings together the power of **Git** and **WP-CLI** into a single, user-friendly graphical interface, reducing the need to work with the command line and helping you avoid common development pitfalls.

With wpCliMate, developers can manage their entire WordPress workflow — from pushing code between environments to replacing URLs and running WP-CLI commands — all from one intuitive interface.

---

## 🚀 Key Features

### 🔧 WP-CLI Integration via GUI
- Easily replace site URLs across environments (`wp search-replace`)
- Activate or deactivate maintenance mode (`wp maintenance-mode`)
- Execute **custom WP-CLI commands** through a graphical interface
- Run common operations on plugins, themes, users, and the database

### 🔁 Git Integration (via JGit)
- Clone, push, and pull Git repositories without leaving the app
- Set and manage multiple Git remotes (useful for local → remote sync)
- Track changes and view current branch status

### 🌐 Multi-Environment Management *(in progress)*
- Define and manage multiple environments (e.g. local, staging, production)
- Save environment-specific configurations
- Execute remote commands via SSH

### 🧠 Automation & Safety
- Auto-backup database and files before critical operations
- Log and display details of all operations executed
- Save user preferences and configuration profiles

---

## 🎯 Project Goals
- Simplify Git workflows for WordPress developers
- Provide easy access to WP-CLI features without using the terminal
- Support multi-environment development and deployment
- Automate common repetitive tasks and reduce configuration errors

---

## 👥 Target Users
- WordPress developers and freelancers
- Web agencies managing multiple sites
- Sysadmins looking for a simplified WP-CLI interface
- Students or entry-level devs learning Git + WordPress

---

## 🧰 Tech Stack

- **Java** (with Maven or Gradle)
- **JavaFX** for the GUI
- **JGit** for Git operations
- **ProcessBuilder** for executing WP-CLI commands
- Optional: **SQLite** or **JSON** for user config storage

---

## 📅 Roadmap

### 🔜 Phase 1 – MVP *(4–6 weeks)*
- Implementation of Core utilities
- Execute key WP-CLI commands
- Git integration: clone, push, pull

### 🔜 Phase 2 – Advanced Features *(6–8 weeks)*
- Automatic backups (database & files)
- Multi-environment support
- Custom WP-CLI command builder

### 🔜 Phase 3 – Optimization & UX *(4–6 weeks)*
- Save/load configuration profiles
- Error notifications and operation logs
- Improved UI/UX design

---

## 📦 Installation

⚠️ *Development in progress. A release will be published soon.*

---

---

## 📫 Contact

Project by [ufos03]  
*Feel free to reach out for suggestions or collaborations.*

