//
// RetroDrom Games Companion
// Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

import RetroDromCompanion
import SwiftUI
import UIKit

struct ContentView: View {
    let sharedApplication: SharedSharedApplication

    var body: some View {
        // The shared UI lays itself out around the system bars and the keyboard.
        ComposeView(sharedApplication: sharedApplication)
            .ignoresSafeArea()
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    let sharedApplication: SharedSharedApplication

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(sharedApplication: sharedApplication)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
