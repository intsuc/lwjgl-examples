import org.lwjgl.Version.getVersion
import org.lwjgl.glfw.GLFWErrorCallback.createPrint
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import scala.util.Using

type Window = Long

@main def run(): Unit =
  println(s"Hello LWJGL ${getVersion()}!")

  // The window handle.
  val window = init()

  loop(window)

  // Free the window callbacks and destroy the window.
  glfwFreeCallbacks(window)
  glfwDestroyWindow(window)

  // Terminate GLFW and free the error callback.
  glfwTerminate()
  glfwSetErrorCallback(null).free()

private def init(): Window =
  // Setup an error callback. The default implementation
	// will print the error message in System.err.
  createPrint(System.err).set()

  // Initialize GLFW. Most GLFW functions will not work before doing this.
  if !glfwInit() then throw IllegalStateException("Unable to initialize GLFW")

  // Configure GLFW.
  glfwDefaultWindowHints() // optional, the current window hints are already the default
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

  // Create the window.
  val window: Window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
  if window == NULL then throw RuntimeException("Failed to create the GLFW window")

  // Setup a key callback. It will be called every time a key is pressed, repeated or released.
  glfwSetKeyCallback(window, (window, key, scancode, action, mods) =>
    if key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE then
      glfwSetWindowShouldClose(window, true)
  )

  // Get the thread stack and push a new frame.
  Using(stackPush()) { stack =>
    val pWidth = stack.mallocInt(1) // int*
    val pHeight = stack.mallocInt(1) // int*

    // Get the window size passed to glfwCreateWindow.
    glfwGetWindowSize(window, pWidth, pHeight)

    // Get the resolution of the primary monitor.
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    // Center the window.
    glfwSetWindowPos(
      window,
      (vidmode.width() - pWidth.get(0)) / 2,
      (vidmode.height() - pHeight.get(0)) / 2
    )
  } // the stack frame is popped automatically

  // Make the OpenGL context current.
  glfwMakeContextCurrent(window)
  // Enable v-sync.
  glfwSwapInterval(1)

  // Make the window visible.
  glfwShowWindow(window)

  window

private def loop(window: Window): Unit =
  // This line is critical for LWJGL's interoperation with GLFW's
	// OpenGL context, or any context that is managed externally.
	// LWJGL detects the context that is current in the current thread,
	// creates the GLCapabilities instance and makes the OpenGL
	// bindings available for use.
  GL.createCapabilities()

  // Set the clear color.
  glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

  // Run the rendering loop until the user has attempted to close
	// the window or has pressed the ESCAPE key.
  while !glfwWindowShouldClose(window) do
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

    glfwSwapBuffers(window) // swap the color buffers

    // Poll for window events. The key callback above will only be
		// invoked during this call.
    glfwPollEvents()
