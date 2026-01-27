const pako = require('pako');

function encodeMermaid(mermaidCode) {
  const state = {
    code: mermaidCode,
    mermaid: '{\n  "theme": "default"\n}',
    updateEditor: false,
    autoSync: true,
    updateDiagram: false
  };
  const json = JSON.stringify(state);
  const data = new TextEncoder().encode(json);
  const compressed = pako.deflate(data, { level: 9 });
  const base64 = Buffer.from(compressed).toString('base64');
  return `https://mermaid.live/edit#pako:${base64}`;
}

const code = `sequenceDiagram
    participant A
    participant B
    A->>B: Hello
`;

console.log(encodeMermaid(code));
