#pragma once

#include <SFML/Graphics.hpp>
#include <SFML/Graphics/RenderWindow.hpp>
#include <SFML/Window/Event.hpp>
#include <SFML/Window/VideoMode.hpp>

class Game {
 private:
  sf::RenderWindow window;
  sf::Event event;

 public:
  Game() : window(sf::VideoMode(800, 600), ""), event() {}

  void init();
  void update();
  void dispose();

  sf::RenderWindow& getWindow() { return this->window; }
  const sf::Event& getEvent() { return this->event; }

  ~Game() {}
};
