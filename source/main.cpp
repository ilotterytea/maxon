#include <iostream>
#include <string>

#include <SFML/Graphics.hpp>
#include <SFML/Graphics/Color.hpp>
#include <SFML/Graphics/RenderWindow.hpp>
#include <SFML/Window/Event.hpp>
#include <SFML/Window/Keyboard.hpp>

#include "lib.hpp"

auto main() -> int
{
  auto const lib = library {};
  auto const message = "Hello from " + lib.name + "!";
  std::cout << message << '\n';

  sf::RenderWindow window(sf::VideoMode(800, 600), "Maxon Petting Simulator");
  window.setFramerateLimit(60);
  sf::Event event;

  while (window.isOpen()) {
    if (window.pollEvent(event)) {
      switch (event.type) {
        case sf::Event::EventType::Closed: {
          window.close();
          break;
        }
        case sf::Event::EventType::KeyPressed: {
          switch (event.key.code) {
            case sf::Keyboard::Escape: {
              window.close();
              break;
            }
            default: {
              break;
            }
          }
        }
        default: {
          break;
        }
      }
    }

    window.clear(sf::Color(255, 182, 193));
    window.display();
  }

  return 0;
}
