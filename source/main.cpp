#include "Game.hpp"

auto main() -> int {
  Game game;

  game.init();

  while (game.getWindow().isOpen()) {
    game.update();
  }

  game.dispose();

  return 0;
}
