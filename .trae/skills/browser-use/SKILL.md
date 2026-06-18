---
name: "browser-use"
description: "Performs browser automation, web testing, and page interactions. Invoke when user needs to test web applications, verify UI changes, or perform browser-based automation tasks."
---

# Browser Use Skill

## Description

This skill provides browser automation capabilities for testing web applications and performing web-based tasks.

## Features

- Navigate web pages
- Interact with HTML elements
- Fill forms automatically
- Take screenshots
- Execute JavaScript on pages
- Verify page content

## Usage Guidelines

### When to Use

Invoke this skill when:
- User wants to test web application functionality
- User needs to verify UI changes or layout
- User wants to automate repetitive browser tasks
- User needs to capture screenshots of web pages
- Testing form submissions or user flows

### Required Parameters

1. **description**: A short description of the task (3-5 words)
2. **query**: Detailed automation instructions including:
   - Target URL to navigate to
   - Actions to perform
   - Expected results to verify
3. **response_language**: Language for the response

### Example

To test a login page:
```
Description: Test login
Query: Navigate to http://localhost:8080/login, fill username 'test' and password '123456', click login button, verify redirect to dashboard
Response_language: English
```

## Notes

- Always provide the exact URL to navigate to
- Be specific about elements to interact with
- Include expected values for verification tasks
- For verification-only tasks, explicitly state "Do NOT modify any source code files"