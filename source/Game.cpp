#include "Game.hpp"

#include <SFML/Graphics/RenderWindow.hpp>
#include <SFML/Window/Event.hpp>
#include <SFML/Window/Keyboard.hpp>
#include <iostream>

void handle_event(Game* game) {
  const sf::Event& event = game->getEvent();
  sf::RenderWindow& window = game->getWindow();

  switch (event.type) {
    case sf::Event::Closed: {
      window.close();
      break;
    }

    case sf::Event::KeyPressed: {
      switch (event.key.code) {
        case sf::Keyboard::Escape: {
          window.close();
          break;
        }
        default: {
          break;
        }
      }
      break;
    }

    default:
      break;
  }
}

void Game::init() {}

void Game::update() {
  if (this->window.pollEvent(this->event)) {
    handle_event(this);
  }
}

void Game::dispose() {}
