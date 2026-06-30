import os
import re

def fix_imports(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    original_content = content

    if '@Positive' in content and 'import jakarta.validation.constraints.Positive;' not in content:
        content = content.replace('import jakarta.validation.constraints.', 'import jakarta.validation.constraints.Positive;\nimport jakarta.validation.constraints.', 1)

    if '@Pattern' in content and 'import jakarta.validation.constraints.Pattern;' not in content:
        content = content.replace('import jakarta.validation.constraints.', 'import jakarta.validation.constraints.Pattern;\nimport jakarta.validation.constraints.', 1)

    if content != original_content:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Fixed {filepath}")

for root, dirs, files in os.walk('.'):
    for file in files:
        if file.endswith('.java'):
            fix_imports(os.path.join(root, file))
