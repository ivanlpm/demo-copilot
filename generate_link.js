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
  const base64 = Buffer.from(compressed).toString('base64')
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
  return `https://mermaid.live/edit#pako:${base64}`;
}

const code = `sequenceDiagram
    participant U as User
    participant FE as Frontend (Home/useDuck)
    participant BC as DuckController
    participant BS as DuckService
    participant EXT as External Duck API
    participant DB as DuckRepository (H2)

    U->>FE: Clicks "Get Another Duck"
    activate FE
    FE->>BC: GET /api/duck
    activate BC
    BC->>BS: getRandomDuck()
    activate BS
    
    BS->>EXT: GET /random
    activate EXT
    EXT-->>BS: returns {url, message}
    deactivate EXT

    BS->>DB: save(new Duck(url, message))
    activate DB
    DB-->>BS: duck persisted
    deactivate DB

    BS-->>BC: returns DuckResponse
    deactivate BS
    BC-->>FE: returns JSON {url, message}
    deactivate BC

    FE-->>U: Updates UI with DuckCard
    deactivate FE`;

console.log(encodeMermaid(code));
