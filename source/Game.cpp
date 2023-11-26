#include "Game.hpp"

#include <SFML/Graphics/RenderWindow.hpp>
#include <SFML/Window/Event.hpp>
#include <SFML/Window/Keyboard.hpp>
#include <iostream>

void handle_event(Game* game) {
  const sf::Event& event = game->get_event();
  sf::RenderWindow& window = game->get_window();

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

void Game::init() { this->m_asset_manager.load_texture("assets/icon.png"); }

void Game::run() {
  while (this->m_window.isOpen()) {
    if (this->m_window.pollEvent(this->m_event)) {
      handle_event(this);
    }
  }
}

void Game::dispose() {}
