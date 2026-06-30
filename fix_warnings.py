import os
import re

def fix_autowired_and_imports(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    original_content = content

    # 1. Expand spring web annotations
    web_annotations = ['RestController', 'RequestMapping', 'GetMapping', 'PostMapping', 'PutMapping', 'DeleteMapping', 'PathVariable', 'RequestBody', 'RequestParam']
    web_imports = []
    for ann in web_annotations:
        if f'@{ann}' in content:
            web_imports.append(f'import org.springframework.web.bind.annotation.{ann};')
    
    if web_imports:
        content = re.sub(r'import org\.springframework\.web\.bind\.annotation\.\*;\n?', '\n'.join(web_imports) + '\n', content)
    else:
        content = re.sub(r'import org\.springframework\.web\.bind\.annotation\.\*;\n?', '', content)

    # 2. Expand jakarta persistence
    persistence_annotations = ['Entity', 'Table', 'Id', 'GeneratedValue', 'GenerationType', 'Column', 'ManyToOne', 'JoinColumn', 'Transient', 'PrePersist', 'PreUpdate']
    persistence_imports = []
    for ann in persistence_annotations:
        if re.search(r'\b' + ann + r'\b', content):
            persistence_imports.append(f'import jakarta.persistence.{ann};')
    
    if persistence_imports:
        content = re.sub(r'import jakarta\.persistence\.\*;\n?', '\n'.join(persistence_imports) + '\n', content)
    else:
        content = re.sub(r'import jakarta\.persistence\.\*;\n?', '', content)

    # 3. Expand jakarta validation
    validation_annotations = ['NotBlank', 'NotNull', 'Email', 'Min', 'Max', 'Size', 'Valid']
    validation_imports = []
    for ann in validation_annotations:
        if f'@{ann}' in content:
            validation_imports.append(f'import jakarta.validation.constraints.{ann};')
    
    if validation_imports:
        content = re.sub(r'import jakarta\.validation\.constraints\.\*;\n?', '\n'.join(validation_imports) + '\n', content)
    else:
        content = re.sub(r'import jakarta\.validation\.constraints\.\*;\n?', '', content)

    # 4. Remove @Autowired and use constructor injection
    if '@Autowired' in content:
        # Remove import
        content = re.sub(r'import org\.springframework\.beans\.factory\.annotation\.Autowired;\n?', '', content)
        
        # Find the class name
        class_match = re.search(r'public class (\w+)', content)
        if class_match:
            class_name = class_match.group(1)
            
            # Find the autowired field
            field_match = re.search(r'[ \t]*@Autowired\n[ \t]*private\s+(\w+)\s+(\w+);', content)
            if field_match:
                field_type = field_match.group(1)
                field_name = field_match.group(2)
                
                # Replace the field declaration with final
                content = content.replace(field_match.group(0), f'    private final {field_type} {field_name};')
                
                # Create constructor
                constructor = f'\n    public {class_name}({field_type} {field_name}) {{\n        this.{field_name} = {field_name};\n    }}\n'
                
                # Insert constructor after the field
                content = re.sub(rf'(private final {field_type} {field_name};)', r'\1\n' + constructor, content)

    if content != original_content:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Fixed {filepath}")

for root, dirs, files in os.walk('.'):
    for file in files:
        if file.endswith('.java'):
            fix_autowired_and_imports(os.path.join(root, file))
