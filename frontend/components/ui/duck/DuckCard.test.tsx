import { render, screen } from '@testing-library/react'
import { DuckCard } from './DuckCard'

describe('DuckCard', () => {
  it('renders loading state correctly', () => {
    render(<DuckCard url="" message="" loading={true} />)
    expect(screen.getByRole('presentation', { hidden: true })).toBeInTheDocument()
  })

  it('renders duck data correctly', () => {
    render(<DuckCard url="https://duck.com/1.jpg" message="Cool duck" loading={false} />)
    expect(screen.getByText('Duck Discovery')).toBeInTheDocument()
    expect(screen.getByText('Cool duck')).toBeInTheDocument()
    const img = screen.getByAltText('Random Duck') as HTMLImageElement
    expect(img.src).toContain('https://duck.com/1.jpg')
  })
})
