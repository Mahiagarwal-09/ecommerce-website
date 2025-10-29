# Contributing to Shri Balaji Attire

Thank you for considering contributing to Shri Balaji Attire! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect differing viewpoints and experiences

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce** the issue
- **Expected behavior**
- **Actual behavior**
- **Screenshots** if applicable
- **Environment details** (OS, browser, versions)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear title and description**
- **Use case** - why is this enhancement useful?
- **Proposed solution**
- **Alternative solutions** you've considered

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes**
4. **Test thoroughly**
5. **Commit with clear messages** (`git commit -m 'Add amazing feature'`)
6. **Push to your fork** (`git push origin feature/amazing-feature`)
7. **Open a Pull Request**

## Development Setup

See [README.md](README.md) for detailed setup instructions.

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/ShriBalajiAttire.git
cd ShriBalajiAttire

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/ShriBalajiAttire.git

# Create feature branch
git checkout -b feature/my-feature
```

## Coding Standards

### Backend (Java/Spring Boot)

- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Write meaningful variable and method names
- Add JavaDoc for public methods
- Keep methods small and focused
- Use dependency injection
- Handle exceptions properly

**Example:**
```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Retrieves a product by its ID.
     * @param id the product ID
     * @return ProductDTO
     * @throws RuntimeException if product not found
     */
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDTO.fromProduct(product);
    }
}
```

### Frontend (React)

- Use functional components with hooks
- Follow React naming conventions
- Keep components small and reusable
- Use meaningful prop names
- Add PropTypes or TypeScript types
- Extract complex logic to custom hooks
- Use context for global state

**Example:**
```javascript
const ProductCard = ({ product, onAddToCart }) => {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(price);
  };

  return (
    <div className="card">
      <h3>{product.name}</h3>
      <p>{formatPrice(product.price)}</p>
      <button onClick={() => onAddToCart(product)}>
        Add to Cart
      </button>
    </div>
  );
};
```

## Testing

### Backend Tests

```bash
cd backend
mvn test
```

Write unit tests for:
- Service layer logic
- Repository queries
- Controller endpoints
- Utility functions

**Example:**
```java
@Test
void getProductById_ShouldReturnProduct_WhenProductExists() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    
    ProductDTO result = productService.getProductById(1L);
    
    assertNotNull(result);
    assertEquals("Test Product", result.getName());
}
```

### Frontend Tests

```bash
cd frontend
npm run lint
npm run build
```

## Commit Messages

Follow conventional commits format:

```
type(scope): subject

body

footer
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(products): add product filtering by color

fix(checkout): resolve payment processing error

docs(readme): update installation instructions
```

## Branch Naming

- `feature/feature-name` - New features
- `fix/bug-description` - Bug fixes
- `docs/documentation-update` - Documentation
- `refactor/code-improvement` - Refactoring
- `test/test-description` - Tests

## Pull Request Guidelines

### Before Submitting

- [ ] Code follows project style guidelines
- [ ] Tests pass locally
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] No console.log or debug code
- [ ] Commit messages follow convention
- [ ] Branch is up to date with main

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
How has this been tested?

## Screenshots (if applicable)

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings
- [ ] Tests added/updated
- [ ] Tests pass
```

## Code Review Process

1. **Automated checks** must pass (CI/CD)
2. **At least one approval** from maintainers
3. **All conversations resolved**
4. **No merge conflicts**

## Project Structure

```
ShriBalajiAttire/
├── backend/
│   ├── src/main/java/com/shribalajiattire/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security components
│   │   ├── service/         # Business logic
│   │   └── exception/       # Exception handlers
│   └── src/test/            # Tests
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable components
│   │   ├── context/         # React context
│   │   ├── pages/           # Page components
│   │   ├── utils/           # Utilities
│   │   └── App.jsx          # Main app
│   └── public/              # Static assets
└── docs/                    # Documentation
```

## Adding New Features

### Backend Feature

1. Create entity in `model/`
2. Create repository in `repository/`
3. Create DTO in `dto/`
4. Implement service in `service/`
5. Create controller in `controller/`
6. Add tests
7. Update documentation

### Frontend Feature

1. Create component in `components/` or `pages/`
2. Add API calls in `utils/api.js`
3. Update routing in `App.jsx`
4. Add context if needed
5. Update documentation

## Documentation

- Update README.md for major changes
- Update API.md for API changes
- Add inline comments for complex logic
- Update DEPLOYMENT.md for deployment changes

## Getting Help

- Open an issue for questions
- Join discussions
- Check existing issues and PRs
- Read documentation thoroughly

## Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Project documentation

Thank you for contributing! 🎉
