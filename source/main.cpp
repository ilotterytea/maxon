#include "Game.hpp"

auto main() -> int {
  Game game;

  game.init();
  game.run();
  game.dispose();

  return 0;
}
