# Workday NASA Images App

## Setup
### Required build tools
* Gradle Version 8.0
* Android Gradle Plugin Version 8.0

### Build and run
1. Open root project in Android Studio (Flamingo and later [support Android Gradle Plugin 8.0](https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility)).
2. Gradle sync
3. Build and run `app` on an Android device/emulator with Android 8.0 or later.

## Libraries
* **RxJava/RxAndroid** for asynchronous operations because I have been mostly working with Rx for development 
* **Jetpack Compose** for declarative UI as it offers a better and faster development experience over Views
* **Retrofit** for networking for its simple setup
* **Coil** for easy URL-based image loading in Jetpack Compose
* **Mockito** for easily mocking dependencies in tests

## Architecture

### Clean Architecture
[Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) encourages separation of concerns between the domain, data, and presentation layers. The inner domain layer is not exposed to presentation concerns such as UI frameworks or data retrieval concerns such as network communication libraries. It only relies on abstractions/interfaces which the outer layers depend on and implement.

By following this architecture, we aim to solve the problem of tight coupling of presentation, domain and data concerns. Because the layers are split by interfaces, it allows for us to build upon or experiment on the outer layers while maintaining the integrity of the domain layer. 

The trade-off of implementing Clean Architecture is that it may be overkill given the scope of the problem. This pattern involves creating separate representations of a domain object in multiple layers, being presentation and data. It also involves building mappers to convert them across the layers. These are additional lines of code and thus additional potential points of failure (this can be addressed with thorough unit testing). I may have had more time to polish the UI if I had followed a more basic view-viewmodel-repository setup.

### Reactive ViewModel's (VM)
I followed this structure of unidirectional data flow VM (insipired by Kaushik Gopal's [reactive VM concept](https://kau.sh/ppt/architecting-android-and-ios-app-features-for-2020/)) as I have been working with it extensively and it is a great solution for view state management. It presents the VM as a "function" that has input and output. It exposes a single method called `process()` which accepts a `ViewIntent`. An intent internally results in a new `ViewState` or emits a `ViewEffect`. The view observes the state and effect emissions to update itself.

Testing this VM is also intuitive. As shown in the project, it can be tested by providing a `ViewIntent` and asserting on the emitted `ViewState`'s or `ViewEffect`'s (much like testing a function).

## Shortcuts
I kept the detail screen as simple as possible, and conciously took some shortcuts to do so:
1. I opted not to use a separate view state object for the detail view. This is not ideal as we are coupling the item view with the details view. Since the data in the view state is minimal, however, I chose to do so to save some time.
2. I opted not to use a ViewModel in the detail screen as it has no user interact-able elements and it makes no network calls.
3. The detail screen uses the thumbnail URL provided by the list view's network call. Thumbnail images are generally not great to display on a full-screen view, but I did so to avoid additional network calls and to save some time. The API is also inconsistent in terms of high res image availability for each item, so I also saved time by avoiding additional parsing of the `/image` endpoint for each item.
4. I opted not to use a dependency injection (DI) library such as Dagger to save myself some setup time. Manual DI using an `AppContainer` was simple and functional, and the time saved by using it allowed me to focus on more valuable areas such as architecture, tests, and UI quality.

## Testing
I wrote unit tests only if they provided value: tests are included for the logic in the repository and ViewModel, but omitted for other classes as they mostly act as passthroughs without much logic or mutations.

## Improvements
If I had more time, I would have liked to do the following:
* Make the detail view more modular; a separate REST call to get more details and higher res image for the detail view, instead of passing the item view's view state through the `Intent`. This would involve creating a new view state and VM for the screen, a new use case, and upgrading the data layer.
* UI enhancements, maybe a hero animation from the list view to the detail view
* Snapshot testing 
* Custom theme
* Potentially use Dagger for more scalable DI
