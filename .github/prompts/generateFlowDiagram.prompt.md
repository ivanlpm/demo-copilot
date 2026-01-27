---
name: generateFlowDiagram
description: Generate a Mermaid sequence diagram showing the end-to-end flow between frontend and backend components.
argument-hint: frontend and backend source files
---
Analyze the provided frontend and backend code to understand the end-to-end data flow for the specified functionality. Identify the key participants and steps, including:
- User interactions with the frontend.
- Frontend state changes or hook triggers.
- HTTP requests from frontend to backend.
- Backend controller and service logic.
- Database operations or external API integrations.
- Responses returning through the stack to the UI.

Generate a Mermaid sequence diagram that visualizes this flow clearly by calling the **Mermaid MCP tool**. The output must include:
- A rendered visual representation of the diagram (ensure the Mermaid code block is correctly formatted so the platform can render it as an image).
- The Mermaid diagram code block itself.
- A direct link to edit the diagram in the [Mermaid Live Editor](https://mermaid.live). The link must include the diagram code encoded using the `#pako:` format (e.g., `https://mermaid.live/edit#pako:...`) so the user can edit it immediately.

Use professional and descriptive labels for each participant and interaction.