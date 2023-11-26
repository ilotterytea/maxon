#pragma once

#include <SFML/Graphics.hpp>
#include <SFML/Graphics/RenderWindow.hpp>
#include <SFML/Window/Event.hpp>
#include <SFML/Window/VideoMode.hpp>

#include "AssetManager.hpp"

class Game {
  sf::RenderWindow m_window;
  sf::Event m_event;

  Maxon::AssetManager m_asset_manager;

 public:
  Game() : m_window(sf::VideoMode(800, 600), ""), m_event() {}

  void init();
  void run();
  void dispose();

  auto get_window() -> sf::RenderWindow& { return this->m_window; }
  auto get_event() -> const sf::Event& { return this->m_event; }

  ~Game() {}
};
