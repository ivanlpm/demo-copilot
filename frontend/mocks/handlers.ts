import { http, HttpResponse } from 'msw'

export const handlers = [
  http.get('http://localhost:8080/api/duck', () => {
    return HttpResponse.json({
      url: 'https://duck.com/mock.jpg',
      message: 'Mocked Duck!'
    })
  }),
]
