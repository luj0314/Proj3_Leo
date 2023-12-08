# Build Your Own World Design Document

**Partner 1: Mike Martinez**
**Partner 2: Leo Lu**

## Classes and Data Structures
World Generator -- Takes in a seed and creates a world that randomly generates rooms and hallways with random parameters.
worldWidth is the number of tiles in the width of the game window. 
worldHeight is the number of tiles in the height of the game window.
maxRoomsLimit is the maximum number of rooms that can be created to avoid overcrowding.
minRoomsLimit is the minimum number of rooms that can be created to avoid emptiness.
maxRoomWidth, maxRoomHeight is the maximum dimension of the rooms to avoid overcrowding.
minRoomWidth, minRoomHeight is the minimum dimension of the rooms to avoid emptiness.
maxRooms is the actual number of rooms the game will create.
TETile[][] WORLD is a two dimensional array that allows renderers to create the world.
Room[] rooms is an array that contains all the information about each room.
EdgeWeightedGraph graph represents a graph with rooms as its vertices and its distance to others as its edges.
nothingTile, floorTile, wallTile is a more convenient way to access Tile. functions.
Random generator is a random type which is created upon a seed that can later be used to access random numbers.
roomsSoFar is the number of room created so far. 

Room -- Representation of a room containing its x and y coordinates (bottom left corner) and its height/width
int roomWidth - width of the room
int roomHeight - height of the room
int xPos - x position of the bottom left corner
int yPos - y position of the bottom left corner
int centerX - x position of the bottom center
int centerY - y position of the left center
int buffer - optional paramter that can be used to separate rooms further

## Algorithms
World Generator:
getWorld() - returns the world 2D array associated with the WorldGenerator
fillWorld() - fills out the world array with the appropriate tiles for each room and generates hallways that are processed by the Prim algorithm
createMap() - creates new rooms and adds them to a WeightedEdgeGraph for processing later
addHallways() - creates a hallway between two rooms and fills out the respective tiles in world 2D array.
findDistance() - helper function that computes the eucledian distance between two rooms
fillRoom() - fills in the room tiles in world with floor tiles
fillPerimeter() - fills in the perimeter of the world with wall tiles
createNewRoom() - creates a new room with random parameters

Room:
overlap() - checks if the current room overlaps with any existing rooms within a buffer

## Persistence
We plan to save the world by saving the 2D array world that contains the tiles to give a sense of continuity into a separate file. We can also save the number generator to an external file along with user position. This would be everything that we would need because the world would still contain the positin of a "key" and a "door" for instance.
